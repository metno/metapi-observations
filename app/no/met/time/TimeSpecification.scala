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

package no.met.time

import com.github.nscala_time.time.Imports._
import scala.util._

/**
 * Metno's Interval parsing mechanism, extending ISO8601 standard with repeating time periods
 */
object TimeSpecification {

  // TODO: Is there a better way to do his?
  DateTimeZone.setDefault(DateTimeZone.UTC);

  /**
   * Parsing output type
   */
  type Range = Seq[Interval]

  /**
   * Attempt to turn the given String into a Range
   */
  def parse(spec: String): Try[Range] = Try {
    if (!spec.contains("/")) {
      val t = DateTime.parse(spec)
      Seq(t to t)
    } else {
      List(Interval.parse(spec))
    }
  }

  /**
   * Find earliest time in the given Range
   */
  def min(times: Range): DateTime = {
    def min(t: Seq[DateTime]): DateTime = {
      t.fold(t(0)) { case (current: DateTime, t: DateTime) => if (t < current) t else current }
    }
    min(times.map(_.getStart))
  }

  /**
   * Find latest time in the given range
   */
  def max(times: Range): DateTime = {
    def max(t: Seq[DateTime]): DateTime = {
      t.fold(t(0)) { case (current: DateTime, t: DateTime) => if (t > current) t else current }
    }
    max(times.map(_.getEnd))
  }
}
