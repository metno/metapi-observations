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

import com.github.nscala_time.time.Imports._
import scala.annotation.tailrec
import scala.language.postfixOps
import no.met.time.TimeSpecification
import no.met.time.TimeSpecification._
import models._

/**
 * Access to a kdvh database - interface definition
 */
abstract class DatabaseAccess {

  //@throws[Exception]("in case something went wrong")
  //def observations(auth:Option[String], sources: Seq[Int], reftime: TimeSpecification.Range, elements: Seq[String], fields: Set[Field]): Seq[ObservationSeries]

  /**
   * Get data from the kdvh database.
   *
   * @param stationId id of station to query
   * @param obstime time intervals to retrieve data for; inclusive-exclusive semantics (i.e. from <= t < to) applies to each interval
   * @param elements list of kdvh element names
   * @param withQuality whether to include quality codes
   *
   * @return A sequence of KdvhQueryResult objects, containing the requested data
   */
  def getObservations(elemTranslator: ElementTranslator, auth: Option[String], stationId: Seq[String], obstime: TimeSpecification.Range, elements: Seq[String], withQuality: Boolean): Seq[ObservationSeries]
  /**
   * Get time series data from the kdvh database.
   *
   * @param stationId id of station to query
   * @param elements list of kdvh element names
   *
   * @return A sequence of KdvhQueryResult objects, containing the requested data
   */
  def getTimeSeries(elemTranslator: ElementTranslator, auth: Option[String], stationId: Seq[String], elements: Seq[String]): Seq[ObservationTimeSeries]
}

object DatabaseAccess {

  private def sanitize(element: String) {

    val elem = "^[A-Za-z0-9_,]+$".r
    element match {
      case elem(_*) => ;
      case _ => throw new Exception("Invalid element specification: " + element)
    }

  }

  @tailrec
  def sanitize(elements: Traversable[String]) {
    if (elements != Nil) {
      sanitize(elements head)
      sanitize(elements tail)
    }
  }

}
