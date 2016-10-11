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
import no.met.data.{ConfigUtil, SourceSpecification}
import no.met.geometry.Level
import no.met.time.TimeSpecification
import no.met.time.TimeSpecification._
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

  private def getTimeSeries(sources: Seq[String], refTime: TimeSpecification.Range, elements: Seq[String]) : List[ObservationMeta] = {
    
    val parser: RowParser[ObservationMeta] = {
      get[Int]("kdvhStNr") ~
      get[Int]("kdvhSensorNr") ~
      get[Option[Double]]("level") ~
      get[Option[String]]("levelUnit") ~
      get[String]("kdvhElemCode") ~
      get[String]("elementId") ~
      get[Option[String]]("elementUnit") ~
      get[Option[String]]("elementCode") ~
      get[String]("valueTable") ~
      get[Option[String]]("flagTable") ~
      get[String]("performanceCategory") ~
      get[String]("exposureCategory") map {
        case kdvhStnr~kdvhSensor~level~levelUnit~kdvhElem~elemId~elemUnit~elemCode~valueTable~flagTable~perfCat~expCat =>
          ObservationMeta(kdvhStnr,kdvhSensor,if (level.isEmpty || levelUnit.isEmpty) None else Some(Seq(Level(Some("height_above_ground"),level,levelUnit,None))),kdvhElem,elemId,elemUnit,elemCode,valueTable,flagTable,perfCat,expCat)
      }
    }

    val sourcesQ = if (sources.isEmpty) {
      "TRUE"
    } else {
      val sourceStr = SourceSpecification.sql(sources, "stnr", Some("(sensor_nr-1)"))
      s"($sourceStr)"
    }
    val timeStart = TimeSpecification.min(refTime).toString
    val timeEnd = TimeSpecification.max(refTime).toString
    val refTimeQ = s"fromdate <= TO_DATE('$timeStart', '$dateFormat') AND (todate IS NULL OR todate >= TO_DATE('$timeEnd', '$dateFormat'))"
    val elementsQ = if (elements.isEmpty) {
      "TRUE"
    } else {
      val elementList = elements reduce (_ + "', '" + _)
      s"element_id IN ('${elementList}')"
    }

    val query = s"""
      |SELECT
        |stnr AS kdvhStnr,
        |coalesce((sensor_nr-1),0) AS kdvhSensorNr,
        |sensor_level AS level,
        |level_unit AS levelUnit,
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
        |$elementsQ
      |GROUP BY
        |stnr, sensor_nr, sensor_level, level_unit, elem_code, element_id, unit, code_table_name, table_name,
        |flag_table_name, performance_category, exposure_category
      |ORDER BY
        |stnr, sensor_nr, sensor_level, elem_code, element_id;""".stripMargin

    Logger.debug(query)

    DB.withConnection("kdvh") { implicit connection =>
      SQL(query).as( parser * )
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

  private def getObservationDataQuery(dataTable : String, flagTable : Option[String], stationId : String, refTime:Seq[Interval], params:Set[String]) : String = {
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

  private def getDataFromTimeSeries(timeSeries: Map[String, List[ObservationMeta]], refTime: TimeSpecification.Range): List[ObservationSeries] = {

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
        
        Logger.debug(query)

        SQL(query).withResult(getRows(_, meta, params, List.empty[ObservationSeries])) match {
          case Right(x) => obsList = x ::: obsList
          case Left(x) => ;
        }
      }
    }
    
    val groups = obsList.groupBy { seriesMeta => (seriesMeta.sourceId, seriesMeta.geometry, seriesMeta.levels, seriesMeta.referenceTime) }

    val data = groups.map { case (k,v) => ObservationSeries(k._1, k._2, k._3, k._4, Some(v.map(_.observations.getOrElse(Seq[Observation]())).flatten.toSeq ) ) } toList

    data.sortBy( r => (r.sourceId, r.referenceTime) )
  }

  override def getObservations(elemTranslator: ElementTranslator, auth: Option[String], sources: Seq[String], refTime: TimeSpecification.Range, elements: Seq[String], fields: Set[String]): List[ObservationSeries] = {
    val timeSeries = getTimeSeries(sources, refTime, elements)
    val tables = timeSeries.groupBy(_.valueTable)
    getDataFromTimeSeries( tables, refTime )
  }

  /**
   * Retrieve time series data from KDVH/KDVH-proxy 
   */
  override def getAvailableTimeSeries(elemTranslator: ElementTranslator, auth: Option[String], sources: Seq[String], elements: Seq[String], fields: Set[String]): List[ObservationTimeSeries] = {
    
    val parser: RowParser[ObservationTimeSeries] = {
      get[Option[String]]("sourceId") ~
      get[Option[String]]("level_type") ~
      get[Option[Double]]("level_value") ~
      get[Option[String]]("level_unit") ~
      get[Option[String]]("level_codetable") ~
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
        case sourceId~level_type~level_value~level_unit~level_codetable~validFrom~validTo~timeOffset~resultTimeInterval~elementId~unit~codeTable
        ~performanceCategory~exposureCategory~status =>
          ObservationTimeSeries(sourceId, None, Some(Seq(Level(level_type, level_value, level_unit, level_codetable))), validFrom, validTo,
              timeOffset, resultTimeInterval, elementId, unit, codeTable, performanceCategory, exposureCategory, status, None)
      }
    }

    val sourcesQ = if (sources.isEmpty) {
      "TRUE"
    } else {
      val sourceStr = SourceSpecification.sql(sources, "stnr", Some("(sensor_nr-1)"))
      s"($sourceStr)"
    }
    val elementsQ = if (elements.isEmpty) {
      "TRUE"
    } else {
      val elementList = elements reduce (_ + "', '" + _)
      s"element_id IN ('${elementList}')"
    }

    val query = s"""
      |SELECT
        |('SN' || stnr::TEXT || ':' || (sensor_nr-1)) AS sourceId ,
        |'height_above_ground' AS level_type,
        |coalesce(sensor_level, 0) AS level_value,
        |coalesce(level_unit, 'm') AS level_unit,
        |NULL AS level_codetable,
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
        |$elementsQ
      |ORDER BY
        |stnr, sensor_level, elem_code, element_id;""".stripMargin

    Logger.debug(query)

    DB.withConnection("kdvh") { implicit conn =>
      val result = SQL(query).as( parser * )
      result.map ( 
        row => row.copy(uri = Some(ConfigUtil.urlStart + "observations/v0.jsonld?sources=" + row.sourceId.get + "&referencetime=" + row.validFrom.get + "/" + row.validTo.getOrElse("2999-12-31T23:59:59Z") + "&elements=" + row.elementId.get))
      )
    }
  }

}

// scalastyle:on

// $COVERAGE-ON$
