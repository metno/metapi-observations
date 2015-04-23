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

package no.met.kdvh

import anorm._
import java.sql.Connection
import com.github.nscala_time.time.Imports._

//$COVERAGE-OFF$Not testing database queries

/**
 * Concrete implementation of KdvhAccess class, connecting to a real kdvh
 * database.
 */
class KdvhDatabaseAccess(connection: Connection) extends KdvhAccess {

  override def getData(stationId: Int, obstime: Interval, parameters: Seq[String]): Seq[KdvhQueryResult] = {
    var ret = List[KdvhQueryResult]()

    if (!parameters.isEmpty) {

      KdvhAccess.sanitize(parameters)

      val tables = observationTables(stationId, obstime, parameters)

      for ((table, tableParameters) <- tables) {
        val param = tableParameters map ((t) => s"d.$t, q.$t as ${t}_flag") reduce (_ + ", " + _)
        val qualityTable = qualityTableFor(table)

        val query = s"""
        SELECT
          d.stnr,
          TO_CHAR(d.dato, '$dateFormat') AS obstime,
          d.typeid,
          $param
        FROM
          $table d,
          $qualityTable q
        WHERE
          d.stnr = q.stnr AND
          d.dato = q.dato AND
          d.typeid = q.typeid AND
          d.stnr={stnr} AND
          ${timeQueryFragment(obstime)}
        ORDER BY
          d.stnr, d.dato, d.typeid"""
        val result = SQL(query).on(
          "stnr" -> stationId,
          "start" -> obstime.getStart().toString,
          "end" -> obstime.getEnd().toString)()(connection)
        val results = result map (new KdvhQueryResult(_, tableParameters))

        ret = KdvhQueryResult.merge(ret, results)
      }
    }
    ret
  }

  /**
   * Dateformat to use when communicating with oracle database
   */
  private val dateFormat = "YYYY-MM-DD\"T\"HH24:MI:SS.\"000Z\""

  /**
   * Find out what tables contain the requested data for the given time range.
   *
   * @param stationId id of station to query
   * @param obstime time range we want data for from is inclusive, to is exclusive
   * @param parameters list of kdvh parameter names
   * @param connection database connection object
   *
   * @return A map containing table name -> List[parameter name] entries,
   *          signifying what tables contain which ones of the requested
   *          parameters.
   */
  private def observationTables(stationId: Int, obstime: Interval, parameters: Traversable[String]): Map[String, Seq[String]] = {

    val param = parameters reduce (_ + "', '" + _)
    val query = s"""
      SELECT
        distinct table_name, elem_code
      FROM
        t_elem_obs
      WHERE
        stnr={stnr} AND
        elem_code IN ('$param') AND
        fdato <= TO_DATE({start}, '$dateFormat') AND
        (tdato IS NULL OR tdato >= TO_DATE({end}, '$dateFormat'))"""
    val result = SQL(query).on(
      "stnr" -> stationId,
      "start" -> obstime.getStart().toString,
      "end" -> obstime.getEnd().toString)()(connection)

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
  private def timeQueryFragment(obstime: Interval): String = {
    if (obstime.duration == new Duration(0)) {
      s"d.dato = TO_DATE({start}, '$dateFormat')"
    } else {
      s"d.dato >= TO_DATE({start}, '$dateFormat') AND d.dato < TO_DATE({end}, '$dateFormat')"
    }
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
      case _ => throw new Exception("Unrecognized table name");
    }

  }
}

// $COVERAGE-ON$
