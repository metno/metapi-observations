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
import com.github.nscala_time.time.Imports.{ Duration, DateTime }
import no.met.observation.Field._
import org.joda.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder }

object MetaData {
  val metadata = new Metadata {
    def parameterUnit(parameter: String): Option[String] = parameter match {
      case v if v.contains("temperature") => Some("celsius")
      case v if v.contains("precipitation") => Some("mm")
      case _ => None
    }
    def sourceCoordinate(source: String): Option[Geometry] =
      Some(Geometry(Point(9.0, 62.3)))
  }
  val unit = metadata.parameterUnit _
  val geometry = metadata.sourceCoordinate _
}

private case class Helper(common: CommonResult, observations: Traversable[ObservationSeries])

/**
 * Creating a json representation of observation data
 */
object JsonFormat {
  import MetaData._

  private val dateTimeZFormatter = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
    .appendTimeZoneOffset("Z", false, 2, 2)
    .toFormatter()

  def dateTimeWrites(pattern: String = ""): Writes[DateTime] = pattern match {
    case s if !s.isEmpty() => dateTimeWrites(Some(new DateTimeFormatterBuilder()
      .appendPattern(s)
      .toFormatter()))
    case _ => dateTimeWrites(Some(dateTimeZFormatter))
  }

  def dateTimeWrites(formatter: Option[DateTimeFormatter]): Writes[DateTime] = new Writes[DateTime] {
    def writes(dt: DateTime): JsValue = formatter match {
      case Some(f) => JsString(f.print(dt))
      case None => JsString(dateTimeZFormatter.print(dt))
    }
  }

  def durationWrites: Writes[Duration] = new Writes[Duration] {
    def writes(d: Duration): JsValue = JsNumber(d.getMillis.toDouble / 1000)
  }

  implicit val dateWrite = dateTimeWrites(Some(dateTimeZFormatter))
  implicit val durationWrite = durationWrites

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
      case gt => JsUndefined("Invalid or unimplmented geometry type '" + gt.toString() + "'.")
    }
  }

  implicit val observedElementWrites: Writes[ObservedElement] = new Writes[ObservedElement] {
    def writes(observation: ObservedElement): JsObject = {
      val vals = List("value" -> Json.toJson(observation.value),
        "unit" -> Json.toJson(unit(observation.phenomenon)),
        "qualityCode" -> Json.toJson(observation.quality))

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
    def writes(observation: Observation): JsObject = Json.obj(
      "reftime" -> observation.time,
      "values" -> observation.data)
  }

  implicit val observationSeriesWrites: Writes[ObservationSeries] = new Writes[ObservationSeries] {
    def writes(observation: ObservationSeries): JsObject = Json.obj(
      "@type" -> "DataCollection",
      "source" -> s"KS${observation.source}",
      "level" -> "ground_level",
      "geometry" -> geometry(s"KS${observation.source}"),
      "dataSet" -> observation.observations)
  }

  private implicit val helperWrites: Writes[Helper] = new Writes[Helper] {
    def writes(h: Helper): JsObject = Json.obj(
      "@context" -> h.common.apiCommon.context,
      "@type" -> "Response",
      "apiVersion" -> h.common.apiCommon.apiVersion,
      "license" -> h.common.apiCommon.license,
      "created" -> h.common.created,
      "queryTime" -> h.common.duration,
      "startOffset" -> h.common.startOffset,
      "currentItemCount" -> h.common.currentItemCount,
      "itemsPerPage" -> h.common.itemsPerPage,
      "totalItemCount" -> h.common.totalItemCount,
      "nextLink" -> h.common.nextLink,
      "prevLink" -> h.common.prevLink,
      "data" -> h.observations)
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
   * @param observations The list to create a represenetation of.
   * @return json representation, as a string
   */
  def format(start: DateTime, observations: Traversable[ObservationSeries]): String = {
    val size = dataSize(observations)
    val duration = new Duration(DateTime.now.getMillis() - start.getMillis())
    val helper = Helper(CommonResult(start, duration, 0, size, size, size, None, None), observations)
    val repr = Json.toJson(helper)
    Json.prettyPrint(repr)
  }
}
