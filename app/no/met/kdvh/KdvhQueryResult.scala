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

import anorm.Row
import play.Logger
import scala.util._

case class ObservedData(value: Option[Double], quality: Option[String] = None)

/**
 * Result of a "standard" query to kdvh database.
 */
case class KdvhQueryResult(val stationId: BigDecimal, val date: String,
  val parameter: Map[String, ObservedData]) {

  def header: String = {
    //$COVERAGE-OFF$Throw-away code
    val parameterNames = parameter map (_._1) reduce (_ + "\t" + _)
    "Station\tTime\t\t\t\t" + parameterNames
    // $COVERAGE-ON$
  }

  /**
   * Create a copy of this object, with parameters from both this and the
   * given result. If both objects contain the same parameter, the one from
   * this is preferred.
   *
   * @param other Object to merge with
   * @return this merged with other
   */
  @throws[IllegalArgumentException]("If this.matches(other) == false")
  def mergedWith(other: KdvhQueryResult): KdvhQueryResult = {
    if (!matches(other)) {
      throw new IllegalArgumentException("Nonmatching stations in merge")
    }
    KdvhQueryResult(stationId, date, other.parameter ++ parameter)
  }

  /**
   * Check if objects represent a observations at the same time and place, but
   * not necessarily parameters or source.
   *
   * @param other Object to compare against
   * @return true if objects have same stationId and date, otherwise false
   */
  def matches(other: KdvhQueryResult): Boolean = {
    stationId == other.stationId && date == other.date
  }

  /**
   * Find an element in other, which matches this
   */
  def getMatchingFrom(other: Seq[KdvhQueryResult]): Option[KdvhQueryResult] = {
    other find (_ matches this)
  }

  override def toString: String = {
    //$COVERAGE-OFF$Throw-away code
    var ret = s"$stationId\t$date:\t"
    for (p <- parameter) {
      p._2.value match {
        case Some(v) => ret += v.toString
        case None => ;
      }
      ret += "\t"
    }
    ret
    // $COVERAGE-ON$
  }
}

object KdvhQueryResult {

  /**
   * Construct from the result of a query for at least stnr, data and the given parameters
   *
   * @param row The database result row to read
   * @param parameters Parameter names to extract
   */
  def apply(row: Row, parameters: Seq[String]): KdvhQueryResult = {
    //$COVERAGE-OFF$Not testing database queries

    val r = row.asMap

    val stnr = row[java.math.BigDecimal]("stnr")
    val obstime = row[String]("obstime")
    val data = parameters.foldLeft(Map.empty[String, ObservedData]) {
      (m, v) =>

        val value = row[Option[Double]](v)
        val flags = Try { row[Option[String]](s"${v}_flag") } match {
          case Success(flag) => flag
          case Failure(x) => None
        }

        m + (v -> ObservedData(value, flags))
    }

    new KdvhQueryResult(stnr, obstime, data)
    //$COVERAGE-ON$
  }

  /**
   * Merge two sequences of KdvhQueryResult into one sequence. Each element in
   * the new sequence will either be the result a a mergedWith operation, or,
   * if not possible, just a copy from one of the tables.
   */
  def merge(a: Seq[KdvhQueryResult], b: Seq[KdvhQueryResult]): List[KdvhQueryResult] = {

    var ret = List[KdvhQueryResult]()

    // Merge what can be merged
    for (v <- a) {
      v.getMatchingFrom(b) match {
        case Some(x) => ret = v.mergedWith(x) :: ret
        case None => ret = v :: ret
      }
    }

    // Fill in with remaining entries from Seq b
    for (v <- b) {
      v.getMatchingFrom(a) match {
        case Some(x) => ;
        case None => ret = v :: ret
      }
    }
    ret.sortWith(_.date < _.date)
  }
}
