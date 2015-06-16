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

package no.met.observation

import com.github.nscala_time.time.Imports.DateTime
import scala.collection._

/**
 * Elementname/value pair
 */
case class ObservedElement(phenomenon: String, value: Option[Double], quality: Option[String]) {
  def empty: Boolean = { value == None && quality == None }
}

/**
 * A set of observed values at a specific time. Intended to be used for a single location.
 */
case class Observation(time: DateTime, data: Traversable[ObservedElement]) {}

/**
 * A set of observations for a single location
 */
case class ObservationSeries(source: Int, observations: Seq[Observation]) {}

object Observation {

  /**
   * Shortcut for creating a simple one-timestep observation
   *
   * @param source Data source (typically station)
   * @param time Valid time
   * @param data observed values, of the form name->(value,quality)
   */
  def series(source: Int, time: DateTime, data: Iterable[(String, (Double, Option[String]))]): ObservationSeries = {
    ObservationSeries(source, List(Observation(time, data.map((kv) => ObservedElement(kv._1, Some(kv._2._1), kv._2._2)))))
  }

}
