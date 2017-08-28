/*
    MET-API

    Copyright (C) 2014 met.no
    Contact information:
    Norwegian Meteorological Institute
    Box 43 Blindern
    0313 OSLO
    NORWAY
    E-mail: met-api@met.no

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
    MA 02110-1301, USA
*/

package services.observations

import play.api.Play.current
import play.api.db._
import play.api.libs.ws._
import play.api.libs.json._
import play.Logger
import anorm._
import anorm.SqlParser._
import com.github.nscala_time.time.Imports._
import com.github.nscala_time.time.OrderingImplicits._
import java.sql.Connection
import javax.inject.Singleton
import scala.annotation.tailrec
import scala.concurrent._
import scala.language.postfixOps
import scala.util._
import no.met.data._
import no.met.data.AnormUtil._
import no.met.time.TimeSpecification
import models._
import services.observations._

//$COVERAGE-OFF$ Unit Tests do not cover Database Access

// scalastyle:off method.length

/**
 * Concrete implementation of KdvhDatabaseAccess class, connecting to a real kdvh
 * database.
 */
@Singleton
class KdvhDatabaseAccess extends DatabaseAccess {

  /**
   * ISO-8601 datetime format
   */
  private val dateFormat = "YYYY-MM-DD\"T\"HH24:MI:SS.\"000Z\""

  private def getTimeSeries(
    sources: Seq[String], refTime: TimeSpecification.Range, elements: Seq[String], perfCat: Seq[String], expCat: Seq[String],
    levels: Option[String]): List[ObservationMeta] = {

    val parser: RowParser[ObservationMeta] = {
      get[Int]("kdvhStNr") ~
        get[Int]("kdvhSensorNr") ~
        get[Option[Int]]("level") ~
        get[String]("kdvhElemCode") ~
        get[String]("elementId") ~
        get[Option[String]]("elementUnit") ~
        get[Option[String]]("elementCode") ~
        get[String]("valueTable") ~
        get[Option[String]]("flagTable") ~
        get[String]("performanceCategory") ~
        get[String]("exposureCategory") map {
        case kdvhStnr~kdvhSensor~level~kdvhElem~elemId~elemUnit~elemCode~valueTable~flagTable~perfCat~expCat =>
          ObservationMeta(
            kdvhStnr, kdvhSensor, if (level.isEmpty) None else Some(Level(None, None, level)), kdvhElem, elemId, elemUnit,
            elemCode, valueTable, flagTable, perfCat, expCat)
      }
    }

    val sourcesQ = if (sources.isEmpty) {
      "TRUE"
    } else {
      val sourceStr = SourceSpecification.stationWhereClause(sources, "stnr", Some("(sensor_nr-1)"))
      s"($sourceStr)"
    }
    val timeStart = TimeSpecification.min(refTime).toString
    val timeEnd = TimeSpecification.max(refTime).toString
    val refTimeQ = s"fromdate <= TO_DATE('$timeEnd', '$dateFormat') AND (todate IS NULL OR todate >= TO_DATE('$timeStart', '$dateFormat'))"
    val elementsQ = if (elements.isEmpty) "TRUE" else "element_id IN ({elements})"
    val perfCatQ = if (perfCat.isEmpty) "TRUE" else "performance_category IN ({perfcats})"
    val expCatQ = if (expCat.isEmpty) "TRUE" else "exposure_category IN ({expcats})"
    val levelList = getIntList(levels, "sensor levels")
    val levelsQ = if (levelList.isEmpty) "TRUE" else "sensor_level::text IN ({levels})"

    val query = s"""
      |SELECT
        |stnr AS kdvhStnr,
        |coalesce((sensor_nr-1),0) AS kdvhSensorNr,
        |sensor_level AS level,
        |elem_code AS kdvhElemCode,
        |element_id AS elementId,
        |unit AS elementUnit,
        |code_table_name AS elementCode,
        |table_name AS valueTable,
        |flag_table_name AS flagTable,
        |performance_category AS performanceCategory,
        |exposure_category AS exposureCategory
      |FROM
        |t_elem_map_timeseries
      |WHERE
        |HAS_ACCESS = 1 AND
        |stnr IS NOT NULL AND
        |elem_code IS NOT NULL AND
        |element_id IS NOT NULL AND
        |table_name IS NOT NULL AND
        |$sourcesQ AND
        |$refTimeQ AND
        |$elementsQ AND
        |$perfCatQ AND
        |$expCatQ AND
        |$levelsQ
      |GROUP BY
        |stnr, sensor_nr, sensor_level, elem_code, element_id, unit, code_table_name, table_name,
        |flag_table_name, performance_category, exposure_category
      |ORDER BY
        |stnr, sensor_nr, sensor_level, elem_code, element_id;""".stripMargin

    //Logger.debug(query)

    DB.withConnection("kdvh") { implicit connection =>
      SQL(insertPlaceholders(query, List(("elements", elements.size), ("perfcats", perfCat.size), ("expcats", expCat.size), ("levels", levelList.size))))
        .on(onArg(List(("elements", elements.toList), ("perfcats", perfCat.toList), ("expcats", expCat.toList), ("levels", levelList.map(_.toString)))): _*)
        .as( parser * )
    }
  }

  private def timeQueryFragment(time: Seq[Interval]): String = {
    def sqlStringify(t: Interval): String = {
      if (t.duration == new Duration(0)) {
        s"dato = TO_DATE('${t.getStart}', '$dateFormat')"
      } else {
        s"(dato >= TO_DATE('${t.getStart}', '$dateFormat') AND dato < TO_DATE('${t.getEnd}', '$dateFormat'))"
      }
    }
    val alternatives = time map { sqlStringify _ } reduce { _ + " OR " + _ }
    s"($alternatives)"
  }

  private def getObservationDataQuery(
    dataTable : String, flagTable : Option[String], stationId : String, refTime:Seq[Interval], params:Set[String]) : String = {

    val elems = params.mkString(",")
    /* Note the use of temporary table expressions in order to force oracle_fdw to push down the selections and
     * projections on the Oracle database.
     */
    if (flagTable.isEmpty) {
      val param = params.map(p => s"d.$p, NULL AS ${p}_flag").toSet.mkString(",")
      s"""|WITH
            |d AS (SELECT
              |stnr,
              |dato,
              |$elems
            |FROM
              |$dataTable
            |WHERE
              |stnr IN ($stationId) AND
              |${timeQueryFragment(refTime)})
          |SELECT
            |d.stnr AS stationId,
            |TO_CHAR(d.dato, '$dateFormat') AS referenceTime,
            |$param
          |FROM d
          |ORDER BY
            |stationId, referenceTime""".stripMargin
    }
    else {
      val param = params.map(p => s"d.$p, q.$p AS ${p}_flag").toSet.mkString(",")
      s"""|WITH
            |d AS (SELECT
              |stnr,
              |dato,
              |$elems
            |FROM
              |$dataTable
            |WHERE
              |stnr IN ($stationId) AND
              |${timeQueryFragment(refTime)}),
            |q AS (SELECT
              |stnr,
              |dato,
              |$elems
            |FROM
              |${flagTable.get}
            |WHERE
              |stnr IN ($stationId) AND
              |${timeQueryFragment(refTime)})
          |SELECT
            |d.stnr AS stationId,
            |TO_CHAR(d.dato, '$dateFormat') AS referenceTime,
            |$param
          |FROM d, q
          |WHERE
            |d.stnr = q.stnr AND
            |d.dato = q.dato
          |ORDER BY
            |stationId, referenceTime""".stripMargin
    }
  }

  private def getDataFromTimeSeries(
    auth: Option[String], requestHost: String, elemInfoGetter: ElementInfoGetter, timeSeries: Map[String, List[ObservationMeta]],
    refTime: TimeSpecification.Range): List[ObservationSeries] = {

    @annotation.tailrec
    def getRows(c: Option[Cursor], meta:List[ObservationMeta], params: Set[String], l: List[ObservationSeries]): List[ObservationSeries] = c match {
      case Some(cursor) => {
        getRows(cursor.next, meta, params, l ::: ObservationSeries(cursor.row, meta, params))
      }
      case _ => l
    }

    var obsList = List[ObservationSeries]()

    DB.withConnection("kdvh") { implicit conn =>
      for ( (t, meta) <- timeSeries ) {
        val valueTable = t;
        val flagTable = meta(0).flagTable
        val kdvhStNr = meta.map(_.kdvhStNr).toSet.mkString(",")
        val params = meta.map(_.kdvhElemCode).toSet
        val query = getObservationDataQuery(valueTable, flagTable, kdvhStNr, refTime, params)

        //Logger.debug(query)

        SQL(query).withResult(getRows(_, meta, params, List.empty[ObservationSeries])) match {
          case Right(x) => obsList = x ::: obsList
          case Left(x) => ;
        }
      }
    }

    val groups = obsList.groupBy { seriesMeta => (seriesMeta.sourceId, seriesMeta.geometry, seriesMeta.referenceTime) }

    val obsSeries = groups.map { case (k, v) =>
      ObservationSeries(k._1, k._2, k._3, Some(v.map(_.observations.getOrElse(Seq[Observation]())).flatten.toSeq ) )
    } toList

    val elemInfoMap: Map[String, ElementInfo] = elemInfoGetter.getInfoMap(
      auth, requestHost, obsSeries.map(os => os.observations.getOrElse(Seq[Observation]()).filter(_.elementId.nonEmpty).map(_.elementId.get)).flatten.toSet
    )

    obsSeries
      .map((os: ObservationSeries) => os.copy(
        observations = Some(os.observations.getOrElse(Seq[Observation]()).map((obs: Observation) => obs.copy(
          level = completeLevelInfo(obs.level, obs.elementId.get, elemInfoMap)
        )))
      ))
      .sortBy(os => (os.sourceId, os.referenceTime))
  }

  override def getObservations(
    auth: Option[String], requestHost: String, elemInfoGetter: ElementInfoGetter, sources: Seq[String], refTime: TimeSpecification.Range,
    elements: Seq[String], perfCat: Seq[String], expCat: Seq[String], levels: Option[String],
    fields: Set[String]): List[ObservationSeries] = { // ### fields not used!
    val timeSeries = getTimeSeries(sources, refTime, elements, perfCat, expCat, levels)
    val tables = timeSeries.groupBy(_.valueTable)
    getDataFromTimeSeries(auth, requestHost, elemInfoGetter, tables, refTime)
  }

  /**
   * Retrieve time series data from KDVH/KDVH-proxy
   */
  override def getAvailableTimeSeries(
    auth: Option[String], requestHost: String, elemInfoGetter: ElementInfoGetter, sources: Seq[String], obsTime: Option[TimeSpecification.Range],
    elements: Seq[String], perfCategory: Seq[String], expCategory: Seq[String], levels: Option[String], levelTypes: Option[String], levelUnits: Option[String],
    fields: Set[String]): List[ObservationTimeSeries] = {

    val parser: RowParser[ObservationTimeSeries] = {
      get[Option[String]]("sourceId") ~
        get[Option[Int]]("level") ~
        get[Option[String]]("validFrom") ~
        get[Option[String]]("validTo") ~
        get[Option[String]]("timeOffset") ~
        get[Option[String]]("resultTimeInterval") ~
        get[Option[String]]("elementId") ~
        get[Option[String]]("unit") ~
        get[Option[String]]("codeTable") ~
        get[Option[String]]("performanceCategory") ~
        get[Option[String]]("exposureCategory") ~
        get[Option[String]]("status") map {
        case sourceId~level~validFrom~validTo~timeOffset~resultTimeInterval~elementId~unit~codeTable
          ~performanceCategory~exposureCategory~status =>
          ObservationTimeSeries(sourceId, None, if (level.isEmpty) None else Some(Level(None, None, level)), validFrom, validTo,
            timeOffset, resultTimeInterval, elementId, unit, codeTable, performanceCategory, exposureCategory, status, None)
      }
    }

    val sourcesQ = timeSeriesSources(sources)
    val obsTimeQ = timeSeriesObsTime(obsTime)
    val elementsQ = timeSeriesElements(elements)
    val perfCatQ = timeSeriesPerformanceCategory(perfCategory)
    val expCatQ = timeSeriesExposureCategory(expCategory)

    val query = s"""
      |SELECT
        |('SN' || stnr::TEXT || ':' || (sensor_nr-1)) AS sourceId ,
        |sensor_level AS level,
        |TO_CHAR(fromdate, '$dateFormat') AS validFrom,
        |TO_CHAR(todate, '$dateFormat') AS validTo,
        |time_offset AS timeOffset,
        |RESULT_TIMEINTERVAL AS resultTimeInterval,
        |ELEMENT_ID AS elementId,
        |UNIT AS unit,
        |CODE_TABLE_NAME AS codeTable,
        |PERFORMANCE_CATEGORY AS performanceCategory,
        |EXPOSURE_CATEGORY AS exposureCategory,
        |'Authoritative' AS status
      |FROM
        |t_elem_map_timeseries
      |WHERE
        |HAS_ACCESS = 1 AND
        |stnr IS NOT NULL AND
        |elem_code IS NOT NULL AND
        |element_id IS NOT NULL AND
        |table_name IS NOT NULL AND
        |$sourcesQ AND
        |$obsTimeQ AND
        |$elementsQ AND
        |$perfCatQ AND
        |$expCatQ
      |ORDER BY
        |stnr, sensor_level, elem_code, element_id;""".stripMargin

    //Logger.debug(query)

    DB.withConnection("kdvh") { implicit conn =>
      val elementsList = if (elements.isEmpty) List[String]() else elements.map(id => replaceWildcards(id).toLowerCase)
      val obsTimeSeries = SQL(insertPlaceholders(query, List(("perfcats", perfCategory.size), ("expcats", expCategory.size))))
        .on(onArg(List(("elements", elementsList.toList), ("perfcats", perfCategory.toList), ("expcats", expCategory.toList))): _*)
        .as( parser * )

      val elemInfoMap: Map[String, ElementInfo] = elemInfoGetter.getInfoMap(
        auth, requestHost, obsTimeSeries.filter(_.elementId.nonEmpty).map(_.elementId.get).toSet)

      val levelList = getIntList(levels, "sensor levels")
      val levelInfoRequested = !(levelList.isEmpty && levelTypes.getOrElse("").trim.isEmpty && levelUnits.getOrElse("").trim.isEmpty)

      obsTimeSeries
        .map(ots => ots.copy(
          uri = Some(
            ConfigUtil.urlStart + "observations/v0.jsonld?sources=" + ots.sourceId.get
              + "&referencetime=" + ots.validFrom.get + "/" + ots.validTo.getOrElse("9999-12-31T23:59:59Z")
              + "&elements=" + ots.elementId.get
              + (if (!perfCategory.isEmpty) "&performancecategory=" + perfCategory.mkString(",") else "")
              + (if (!expCategory.isEmpty) "&exposurecategory=" + expCategory.mkString(",") else "")
          ),
          level = completeLevelInfo(ots.level, ots.elementId.get, elemInfoMap)
        ))
        .filter(ots => if (ots.level.isEmpty) {
          !levelInfoRequested // keep iff no level info is requested
        } else {
          val level = ots.level.get
          assert(level.value.nonEmpty)
          (levelList.isEmpty || levelList.contains(level.value.get)) && matchesWords(level.levelType, levelTypes) && matchesWords(level.unit, levelUnits)
        })
    }
  }

  // Returns true iff words is None or w ("" if None) matches any word in the comma-separated list words. The matching is case-insensitive and a word in
  // words is allowed to contain asterisks to represent zero or more characters.
  // ### TBD: Move to util package (used also in records and elements modules)
  private def matchesWords(w: Option[String], words: Option[String]) = {
    words match {
      case Some(wlist) => {
        val w1 = w.getOrElse("").trim.toLowerCase
        wlist.split(",").toSet.exists((w2: String) => w1.matches(w2.trim.toLowerCase.replace("(", "\\(").replace(")", "\\)").replace("*", ".*")))
      }
      case None => true
    }
  }

  // Attempts to complete a sensor level object, returning Some(Level) upon success.
  // Returns None if no level was found. Throws an exception if an inconsistency was detected.
  // scalastyle:off cyclomatic.complexity
  private def completeLevelInfo(level: Option[Level], elementId: String, elemInfoMap: Map[String, ElementInfo]): Option[Level] = {
    level match {
      case Some(sensorLevel) => { // i.e. t_elem_map_timeseries contained a level for this time series
        assert(sensorLevel.levelType.isEmpty)
        assert(sensorLevel.unit.isEmpty)
        assert(sensorLevel.value.nonEmpty)
        Try(elemInfoMap(elementId)) match {
          case scala.util.Success(elemInfo) => { // info was found for this element in the elements/ endpoint
            (elemInfo.json \ "sensorLevels").asOpt[JsObject] match {
              case Some(sensorLevels) => { // the info contains sensor level info

                // get the level type
                val ltype: String = (sensorLevels \ "levelType").asOpt[String] match {
                  case Some(x) => x
                  case None => throw new InternalServerErrorException(
                    (s"Sensor level registered in t_elem_map_timeseries for $elementId, " +
                      "but no sensor level type was found for this element in the elements/ endpoint"))
                }

                // get the level unit
                val unit: String = (sensorLevels \ "unit").asOpt[String] match {
                  case Some(x) => x
                  case None => throw new InternalServerErrorException(
                    (s"Sensor level registered in t_elem_map_timeseries for $elementId, " +
                      "but no sensor level unit was found for this element in the elements/ endpoint"))
                }

                // get the official levels and ensure that the level is one of those
                val values: Seq[Int] = (sensorLevels \ "values").asOpt[Seq[Int]] match {
                  case Some(x) => x
                  case None => throw new InternalServerErrorException(
                    (s"Sensor level registered in t_elem_map_timeseries for $elementId, " +
                      "but no sensor levels were found for this element in the elements/ endpoint"))
                }
                if (!values.contains(sensorLevel.value.get)) throw new InternalServerErrorException(
                  (s"The sensor level registered in t_elem_map_timeseries for $elementId (${sensorLevel.value.get}) " +
                    s"doesn't match any of those found for this element in the elements/ endpoint (${values.mkString(", ")})"))

                // validation ok, so insert the level type and unit
                Some(sensorLevel.copy(levelType = Some(ltype), unit = Some(unit)))
              }
              case _ => throw new InternalServerErrorException(
                (s"Sensor level registered in t_elem_map_timeseries for $elementId, " +
                  "but no sensor level info was found for this element in the elements/ endpoint"))
            }
          }
          case _ => throw new InternalServerErrorException(
            (s"Sensor level registered in t_elem_map_timeseries for $elementId, " +
              "but no info at all was found for this element in the elements/ endpoint"))
        }
      }
      case _ => None // no level found in t_elem_map_timeseries for this time series
    }
  }
  // scalastyle:on cyclomatic.complexity

  // Returns a sorted list of integers from an input string of the form "1,7,3" (i.e. a comma-separated list of integers).
  private def getIntList(os: Option[String], itype: String): List[Int] = {

    def toNonNegativeInt(s: String): Int = {
      val psingle = "([0-9]+)".r
      s.trim match {
        case psingle(a) => a.toInt
        case _ => throw new BadRequestException(s"$itype: syntax error: $s not a non-negative integer")
      }
    }

    os match {
      case Some(s) => s.split(",").foldLeft(Set[Int]()) { (acc, cur) => acc + toNonNegativeInt(cur) }.toList.sorted
      case None => List[Int]()
    }
  }

  private def timeSeriesSources(sources: Seq[String]) = {
    if (sources.isEmpty) {
      "TRUE"
    } else {
      val sourceStr = SourceSpecification.stationWhereClause(sources, "stnr", Some("(sensor_nr-1)"))
      s"($sourceStr)"
    }
  }

  private def timeSeriesObsTime(obsTime: Option[TimeSpecification.Range]) = {
    if (obsTime.isEmpty) {
      "TRUE"
    } else {
      val timeStart = TimeSpecification.min(obsTime.get).toString
      val timeEnd = TimeSpecification.max(obsTime.get).toString
      s"(fromdate < TO_DATE('$timeEnd', '$dateFormat') AND (todate IS NULL OR todate > TO_DATE('$timeStart', '$dateFormat')))"
    }
  }

  // Converts a string to use '%' for wildcards instead of '*'.
  private def replaceWildcards(s: String): String = {
    s.replaceAll("\\*", "%")
  }

  private def timeSeriesElements(elements: Seq[String]) = if (elements.isEmpty) {
    "TRUE"
  } else {
    "(" + (1 to elements.size).foldLeft("") { (s: String, i: Int) => s + s"${if (i == 1) "" else " OR "}(lower(element_id) LIKE {elements$i})" } + ")"
  }

  private def timeSeriesPerformanceCategory(perfCat: Seq[String]) = if (perfCat.isEmpty) "TRUE" else "performance_category IN ({perfcats})"

  private def timeSeriesExposureCategory(expCat: Seq[String]) = if (expCat.isEmpty) "TRUE" else "exposure_category IN ({expcats})"

}

// scalastyle:on

// $COVERAGE-ON$
