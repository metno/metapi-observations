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

import play.api.libs.json._

/**
 * Creating a json representation of observation data
 */
object JsonFormat {

  implicit val observedElementWrites: Writes[ObservedElement] = new Writes[ObservedElement] {
    def writes(observation: ObservedElement): JsObject = {
      if (!observation.empty) {
        Json.obj(observation.phenomenon -> Json.obj(
          "value" -> observation.value,
          "quality" -> observation.quality))
      } else {
        Json.obj()
      }
    }
  }

  implicit val observationWrites: Writes[Observation] = new Writes[Observation] {
    def writes(observation: Observation): JsObject = Json.obj(
      "time" -> observation.time.toString("YYYY-MM-dd'T'HH:mm:ss'Z'"),
      "data" -> observation.data)
  }
  implicit val observationSeriesWrites: Writes[ObservationSeries] = new Writes[ObservationSeries] {
    def writes(observation: ObservationSeries): JsObject = Json.obj(
      "source" -> observation.source,
      "observations" -> observation.observations)
  }

  /**
   * Create json representation of the given list
   *
   * @param observations The list to create a represenetation of.
   * @return json representation, as a string
   */
  def format(observations: Traversable[ObservationSeries]): String = {
    val repr = Json.toJson(observations)
    Json.prettyPrint(repr)
  }

}
