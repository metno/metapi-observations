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
import play.Logger
import play.api.db._
import play.api.libs.ws._
import anorm._
import anorm.SqlParser._
import com.github.nscala_time.time.Imports._
import java.sql.Connection
import javax.inject.Singleton
import scala.annotation.tailrec
import scala.concurrent._
import scala.language.postfixOps
import scala.util._
import no.met.time.TimeSpecification
import no.met.time.TimeSpecification._
import models._
import services.observations._

//$COVERAGE-OFF$ Unit Tests do not cover Database Access

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

  /** Retrieve data from KDVH without quality information. 
   */
  private def queryWithoutQuality(table: String, tableParameters: Iterable[String], stationId: String, refTime: Seq[Interval]): String = {
    val param = tableParameters map ("d." + _) reduce (_ + ", " + _)
    
    s"""
        SELECT
          |d.stnr AS station_number,
          |TO_CHAR(d.dato, '$dateFormat') AS reference_time,
          |d.typeid AS type_id,
          |$param
        |FROM
          |$table d
        |WHERE
          |d.stnr=$stationId AND
          |${timeQueryFragment(refTime)}
        |ORDER BY
          |station_number, reference_time, type_id""".stripMargin
  }

  /** Retrieve data from KDVH with quality information.
   */
  private def queryWithQuality(table: String, tableParameters: Iterable[String], stationId: String, refTime: Seq[Interval]): String = {
    val param = tableParameters map (t => s"d.$t, q.$t as ${t}_flag") reduce (_ + ", " + _)
    val qualityTable = qualityTableFor(table)

    s"""
        |SELECT
          |d.stnr as station_number,
          |TO_CHAR(d.dato, '$dateFormat') AS reference_time,
          |d.typeid AS type_id,
          |$param
        |FROM
          |$table d,
          |$qualityTable q
        |WHERE
          |d.stnr = q.stnr AND
          |d.dato = q.dato AND
          |d.typeid = q.typeid AND
          |d.stnr=$stationId AND
          |${timeQueryFragment(refTime)}
        |ORDER BY
          |station_number, reference_time, type_id""".stripMargin
  }

  /** Get the name of the table giving quality information about data.
   * @param dataTableName name of the table we are getting data for
   * @returns the quality table matching the given data table
   */
  @throws[Exception]("in case input parameter seemed wrong")
  private def qualityTableFor(dataTableName: String): String = {
    val tableExpression = "t_(.)data".r

    dataTableName toLowerCase match {
      case tableExpression(t) => s"t_${t}flag";
      case _                  => throw new Exception("Unrecognized table name");
    }
  }


  /** Get the tables containing data for the requested time range and elements.
   *
   * @param stationId id of station to query
   * @param obstime time range we want data for from is inclusive, to is exclusive
   * @param elements list of kdvh element names
   * @param conn database connection object
   *
   * @return A map containing table name -> List[element name] entries, signifying what tables contain which ones of
   * 				 the requested elements.
   */
  private def observationTables(stationId: String, refTime: TimeSpecification.Range, elements: Traversable[String], conn: Connection): Map[String, Seq[String]] = {
    val elem = elements reduce (_ + "', '" + _)
    val query = s"""
      |SELECT
        |distinct table_name, elem_code
      |FROM
        |t_elem_obs
      |WHERE
        |stnr=$stationId AND
        |elem_code IN ('$elem') AND
        |fdato <= TO_DATE({start}, '$dateFormat') AND
        |(tdato IS NULL OR tdato >= TO_DATE({end}, '$dateFormat'))""".stripMargin

    Logger.debug(query)
        
    val result = SQL(query).on(
      "start" -> TimeSpecification.min(refTime).toString,
      "end" -> TimeSpecification.max(refTime).toString)()(conn)

    val ret = collection.mutable.Map[String, List[String]]()

    for (row <- result) {
      val tableName = row[String]("table_name")
      val elemCode = row[String]("elem_code")

      val v = ret.getOrElse(tableName, List[String]())
      val updated = elemCode :: v
      ret += (tableName -> updated)
    }

    ret toMap
  }

  /**
   * Get a fragment of an sql query, specifying the time we want data for
   */
  private def timeQueryFragment(time: Seq[Interval]): String = {
    def sqlStringify(t: Interval): String = {
      if (t.duration == new Duration(0)) {
        s"d.dato = TO_DATE('${t.getStart}', '$dateFormat')"
      } else {
        s"(d.dato >= TO_DATE('${t.getStart}', '$dateFormat') AND d.dato <= TO_DATE('${t.getEnd}', '$dateFormat'))"
      }
    }
    val alternatives = time map { sqlStringify _ } reduce { _ + " OR " + _ }
    s"($alternatives)"
  }

  /** Get observation data for a single source and refTime */
  def getDataForRefTime(stationId: String, refTime: TimeSpecification.Range, elements: Seq[String], withQuality: Boolean): Seq[KdvhQueryResult] = {
    var ret = List[KdvhQueryResult]()

    DB.withConnection("kdvh") { implicit conn =>

      val tables = observationTables(stationId, refTime, elements, conn)

      for ((table, tableParameters) <- tables) {

        val query = withQuality match {
          case true  => queryWithQuality(table, tableParameters, stationId, refTime)
          case false => queryWithoutQuality(table, tableParameters, stationId, refTime)
        }
        
        Logger.debug(query)

        val result = SQL(query)()(conn)
        val results = result map (KdvhQueryResult(_, tableParameters))
        ret = KdvhQueryResult.merge(ret, results)
      }
    }
    ret
  }
  
  private def getDataForSource(elemTranslator: ElementTranslator, auth: Option[String], source: String, refTime: TimeSpecification.Range, elements: Seq[String], withQuality: Boolean): Seq[Observation] = {
    val kdvhElements = elements map (elemTranslator.toKdvhElemName(auth, _))
    Logger.debug("kdvhElements ..." + kdvhElements.toString)
    val databaseResult = getDataForRefTime(source, refTime, kdvhElements.flatten, withQuality)

    databaseResult map {
      (r: KdvhQueryResult) =>
        {
          val time = DateTime.parse(r.date)
          val data = r.element.map { (v) =>
            ObservedElement(elemTranslator.toApiElemName(auth, v._1), v._2.value, None, v._2.quality)
          }
          Observation(time, data toList)
        }
    }
  }
  
  def getObservations(elemTranslator: ElementTranslator, auth: Option[String], sources: Seq[String], refTime: TimeSpecification.Range, elements: Seq[String], withQuality: Boolean): Seq[ObservationSeries] = {
    Logger.debug("KdvhAccess.getObservations() ...")
    
    sources.map((source) => ObservationSeries(source, getDataForSource(elemTranslator, auth, source, refTime, elements, withQuality)))
  }



  /**
   * Retrieve time series data from KDVH/KDVH-proxy 
   */
  override def getTimeSeries(elemTranslator: ElementTranslator, auth: Option[String], stationIds: Seq[String], elements: Seq[String]): List[ObservationTimeSeries] = {
    Logger.debug("KdvhAccess.getTimeSeriesData() ...")
    
    val parser: RowParser[ObservationTimeSeries] = {
      get[Long]("station") ~
      get[Option[Int]]("sensor_number") ~
      get[String]("from_date") ~
      get[Option[String]]("to_date") ~
      get[Option[String]]("kdvh_element") ~
      get[String]("observation_timespan") ~
      get[String]("time_offset") map {
        case sourceId~sensorNr~fromDate~toDate~elementId~obsTimeSpan~timeOffset => ObservationTimeSeries(sourceId.toString, sensorNr, fromDate, toDate, elementId, obsTimeSpan, timeOffset)
      }
    }
    
    DB.withConnection("kdvh") { implicit conn =>
      var stationQ = "TRUE"
      var elemQ = "TRUE"
      if (!stationIds.isEmpty) {
        val stnr = stationIds.mkString(",")
        stationQ = s"""STNR IN ($stnr)"""
      }
      if (!elements.isEmpty) {
        DatabaseAccess.sanitize(elements)
        val elem = elements reduce (_ + "', '" + _)
        elemQ = s"""ELEM_CODE IN ('$elem')"""
      }     
      val query = s"""
          |SELECT
            |STNR AS station,
            |SENSOR_NR AS sensor_number,
            |TO_CHAR(FROMDATE, '$dateFormat') AS from_date,
            |TO_CHAR(TODATE, '$dateFormat') AS to_date,
            |ELEM_CODE AS kdvh_element,
            |COALESCE(OBSERVATION_TIMESPAN, 'T0H0M0S') AS observation_timespan,
            |TIME_OFFSET AS time_offset
          |FROM
            |T_ELEM_MAP_TIMESERIES
          |WHERE
            |HAS_ACCESS = 1 AND
            |$stationQ AND
            |$elemQ
          |ORDER BY
            |station, kdvh_element, from_date""".stripMargin
      Logger.debug(query)
      
      val result = SQL(query).as( parser * )
      result.map ( 
        row => row.copy(sourceId = "SN" + row.sourceId, elementId = elemTranslator.toApiElemName(auth, row.elementId.get))
      )
    }
  }

}

// $COVERAGE-ON$
