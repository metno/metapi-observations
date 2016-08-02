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

import javax.inject.Singleton
import no.met.time._
import anorm.SQL
import play.api.db._
import play.api.Play.current
import play.Logger
import play.api.libs.ws._
import scala.concurrent._
import scala.util._
import java.sql.Connection
import com.github.nscala_time.time.Imports._
import services._
import anorm.NamedParameter.string
import anorm.sqlToSimple
import no.met.kdvh._
import no.met.observation._
import scala.annotation.tailrec

//$COVERAGE-OFF$Not testing database queries

/**
 * Concrete implementation of KdvhDatabaseAccess class, connecting to a real kdvh
 * database.
 */
@Singleton
class KdvhDatabaseAccess extends DatabaseAccess {

  private def queryWithoutQuality(table: String, tableParameters: Iterable[String], stationId: Int, obstime: Seq[Interval]): String = {

    val param = tableParameters map ("d." + _) reduce (_ + ", " + _)

    s"""
        SELECT
          |d.stnr,
          |TO_CHAR(d.dato, '$dateFormat') AS obstime,
          |d.typeid,
          |$param
        |FROM
          |$table d
        |WHERE
          |d.stnr=$stationId AND
          |${timeQueryFragment(obstime)}
        |ORDER BY
          |d.stnr, d.dato, d.typeid""".stripMargin
  }

  private def queryWithQuality(table: String, tableParameters: Iterable[String], stationId: Int, obstime: Seq[Interval]): String = {

    val param = tableParameters map (t => s"d.$t, q.$t as ${t}_flag") reduce (_ + ", " + _)
    val qualityTable = qualityTableFor(table)

    s"""
        |SELECT
          |d.stnr,
          |TO_CHAR(d.dato, '$dateFormat') AS obstime,
          |d.typeid,
          |$param
        |FROM
          |$table d,
          |$qualityTable q
        |WHERE
          |d.stnr = q.stnr AND
          |d.dato = q.dato AND
          |d.typeid = q.typeid AND
          |d.stnr=$stationId AND
          |${timeQueryFragment(obstime)}
        |ORDER BY
          |d.stnr, d.dato, d.typeid""".stripMargin
  }

  override def getData(stationId: Int, obstime: Seq[Interval], elements: Seq[String], withQuality: Boolean): Seq[KdvhQueryResult] = {
    Logger.debug("KdvhAccess.getData() ...")
    Logger.debug("Elements: " + elements.mkString(", "))
    var ret = List[KdvhQueryResult]()

    if (!elements.isEmpty) {

      DatabaseAccess.sanitize(elements)

      DB.withConnection("kdvh") { implicit conn =>

        val tables = observationTables(stationId, obstime, elements, conn)

        for ((table, tableParameters) <- tables) {

          val query = withQuality match {
            case true  => queryWithQuality(table, tableParameters, stationId, obstime)
            case false => queryWithoutQuality(table, tableParameters, stationId, obstime)
          }
          val result = SQL(query)()(conn)
          val results = result map (KdvhQueryResult(_, tableParameters))
          ret = KdvhQueryResult.merge(ret, results)
        }
      }
    }
    ret
  }
  
  /**
   * Date format to use when communicating with oracle database
   */
  private val dateFormat = "YYYY-MM-DD\"T\"HH24:MI:SS.\"000Z\""

  /**
   * Find out what tables contain the requested data for the given time range.
   *
   * @param stationId id of station to query
   * @param obstime time range we want data for from is inclusive, to is exclusive
   * @param elements list of kdvh element names
   * @param conn database connection object
   *
   * @return A map containing table name -> List[element name] entries,
   *          signifying what tables contain which ones of the requested
   *          elements.
   */
  private def observationTables(stationId: Int, obstime: TimeSpecification.Range, elements: Traversable[String], conn: Connection): Map[String, Seq[String]] = {

    val elem = elements reduce (_ + "', '" + _)
    val query = s"""
      |SELECT
        |distinct table_name, elem_code
      |FROM
        |t_elem_obs
      |WHERE
        |stnr={stnr} AND
        |elem_code IN ('$elem') AND
        |fdato <= TO_DATE({start}, '$dateFormat') AND
        |(tdato IS NULL OR tdato >= TO_DATE({end}, '$dateFormat'))""".stripMargin
    val result = SQL(query).on(
      "stnr" -> stationId,
      "start" -> TimeSpecification.min(obstime).toString,
      "end" -> TimeSpecification.max(obstime).toString)()(conn)

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
  private def timeQueryFragment(obstime: Seq[Interval]): String = {
    def sqlStringify(t: Interval): String = {
      if (t.duration == new Duration(0)) {
        s"d.dato = TO_DATE('${t.getStart}', '$dateFormat')"
      } else {
        s"(d.dato >= TO_DATE('${t.getStart}', '$dateFormat') AND d.dato <= TO_DATE('${t.getEnd}', '$dateFormat'))"
      }
    }
    val alternatives = obstime map { sqlStringify _ } reduce { _ + " OR " + _ }
    s"($alternatives)"
  }

  /**
   * Get the name of the table giving quality information about data. (There are several such tables).
   *
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

  /**
   * Retrieve time series data from KDVH/KDVH-proxy
   */
  override def getTimeSeries(stationIds: Seq[Int], elements: Seq[String]): List[TimeSeries] = {
    Logger.debug("KdvhAccess.getTimeSeriesData() ...")
    
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
            |ELEM_CODE AS kdvh_element,
            |TO_CHAR(FROMDATE, '$dateFormat') AS from_date,
            |TO_CHAR(TODATE, '$dateFormat') AS to_date,
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
      val result = SQL(query)()(conn)
      result.map ( row =>
          TimeSeries(
              row[Int]("station"),
              row[Int]("sensor_number"),
              row[String]("kdvh_element"),
              row[String]("from_date"),
              row[Option[String]]("to_date"),
              row[String]("observation_timespan"),
              row[String]("time_offset")
          )
      ).toList
    }
  }

}

// $COVERAGE-ON$
