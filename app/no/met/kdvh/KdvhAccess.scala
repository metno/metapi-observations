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

import com.github.nscala_time.time.Imports._
import scala.annotation.tailrec

/**
 * Access to a kdvh database - interface definition
 */
trait KdvhAccess {
  /**
   * Get data from the kdvh database.
   *
   * @param stationId id of station to query
   * @param obstime time range we want data for from is inclusive, to is exclusive
   * @param parameters list of kdvh parameter names
   * @param connection database connection object
   *
   * @return A sequence of KdvhQueryResult objects, containing the requested data
   */
  def getData(stationId: Int, obstime: Seq[Interval], parameters: Seq[String], withQuality: Boolean): Seq[KdvhQueryResult]
}

object KdvhAccess {

  private def sanitize(parameter: String) {

    val param = "^[a-zA-Z0-9_]+$".r
    parameter match {
      case param(_*) => ;
      case _ => throw new Exception("Invalid parameter specification: " + parameter)
    }

  }

  @tailrec
  def sanitize(parameters: Traversable[String]) {
    if (parameters != Nil) {
      sanitize(parameters head)
      sanitize(parameters tail)
    }
  }

}
