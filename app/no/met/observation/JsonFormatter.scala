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

import play.api.mvc._
import play.api.libs.json._
import com.github.nscala_time.time.Imports.{ Duration, DateTime }
import no.met.observation.Field._
import no.met.data._
import no.met.data.format.json._
import org.joda.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder }

object MetaData {
  val metadata = new Metadata {
    def elementUnit(element: String): Option[String] = element match {
      case v if v.contains("temperature") => Some("celsius")
      case v if v.contains("precipitation") => Some("mm")
      case _ => None
    }
    def sourceCoordinate(source: String): Option[Geometry] =
      Some(Geometry(Point(9.0, 62.3)))
  }
  val unit = metadata.elementUnit _
  val geometry = metadata.sourceCoordinate _
}

/**
 * Creating a json representation of observation data
 */
class JsonFormatter(fields: Set[Field]) extends BasicJsonFormat {
  import MetaData._

  implicit val pointWrite: Writes[Point] = new Writes[Point] {
    def writes(point: Point): JsValue = {
      point.altitude match {
        case Some(a) => Json.toJson(Seq(point.longitude, point.latitude, a))
        case _ => Json.toJson(Seq(point.longitude, point.latitude))
      }
    }
  }

  implicit val geometryWrite: Writes[Geometry] = new Writes[Geometry] {
    def writes(g: Geometry): JsValue = g.geom match {
      case p: Point =>
        Json.obj("type" -> "Point",
          "coordinates" -> p)
      case gt => JsNull
      //Undefined("Invalid or unimplmented geometry type '" + gt.toString() + "'.")
    }
  }

  implicit val observedElementWrites: Writes[ObservedElement] = new Writes[ObservedElement] {
    def writes(observation: ObservedElement): JsObject = {

      val wanted = Set(Field.value, Field.unit, Field.qualityCode)
      val vals = fields filter (wanted contains _) map {
        _ match {
          case Field.value => "value" -> Json.toJson(observation.value)
          case Field.unit => "unit" -> Json.toJson(unit(observation.phenomenon))
          case Field.qualityCode => "qualityCode" -> Json.toJson(observation.quality)
        }
      } toList

      Json.obj(
        observation.phenomenon -> JsObject(
          vals.flatMap { v =>
            v._2 match {
              case JsNull => Nil
              case _ => List(v)
            }
          }))
    }
  }

  implicit val seqObservedElementWrites: Writes[Traversable[ObservedElement]] =
    new Writes[Traversable[ObservedElement]] {
      def writes(seq: Traversable[ObservedElement]): JsValue = JsArray {
        (for (e <- seq if !e.empty) yield Json.toJson(e)).toSeq
      }
    }

  implicit val observationWrites: Writes[Observation] = new Writes[Observation] {
    def writes(observation: Observation): JsObject = {

      var elements = List.empty[(String, JsValue)]
      if (fields.contains(Field.value) || fields.contains(Field.unit) || fields.contains(Field.qualityCode)) {
        elements = "values" -> Json.toJson(observation.data) :: elements
      }
      if (fields contains Field.reftime) {
        elements = "reftime" -> Json.toJson(observation.time) :: elements
      }
      new JsObject(elements.toMap[String, JsValue])
    }
  }
/*
 *       var elements = List.empty[(String, JsValue)]
      if (fields.contains(Field.value) || fields.contains(Field.unit) || fields.contains(Field.qualityCode)) {
        elements = "values" -> Json.toJson(observation.data) :: elements
      }
      if (fields contains Field.reftime) {
        elements = "reftime" -> Json.toJson(observation.time) :: elements
      }
      new JsObject(elements)
 *
 */

  implicit val observationSeriesWrites: Writes[ObservationSeries] = new Writes[ObservationSeries] {
    def writes(observation: ObservationSeries): JsObject = Json.obj(
      "@type" -> "DataCollection",
      "source" -> s"KS${observation.source}",
      "level" -> "ground_level",
      "geometry" -> geometry(s"KS${observation.source}"),
      "dataSet" -> observation.observations)
  }

  implicit val responseDataWrites: Writes[ResponseData] = new Writes[ResponseData] {
    def writes(response: ResponseData): JsObject = {
      header(response.header) + ("data", Json.toJson(response.data))
    }
  }

  private def dataSize(observations: Traversable[ObservationSeries]): Long =
    observations.foldLeft(0) { (sum, v) =>
      sum + v.observations.foldLeft(0) {
        (sum, v) =>
          sum + v.data.foldLeft(0) { (sum, e) =>
            sum + (if (e.empty) 0 else 1)
          }
      }
    }

  /**
   * Create json representation of the given list
   *
   * @param observations The list to create a representation of.
   * @return json representation, as a string
   */
  def format[A](start: DateTime, observations: Traversable[ObservationSeries])(implicit request: Request[A]): String = {
    val size = dataSize(observations)
    val duration = new Duration(DateTime.now.getMillis() - start.getMillis())
    val header = BasicResponseData("Response", "Observations", "v0", duration, size, size, size, 0, None, None)

    val response = ResponseData(header, observations)
    Json.prettyPrint(Json.toJson(response))
  }
}
