/*
    MET-API

    Copyright (C) 2016 met.no
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

import play.api.mvc._
import play.api.libs.json._
import com.github.nscala_time.time.Imports._
import no.met.data.BasicResponseData
import no.met.data.format.json.BasicJsonFormat
import no.met.observation.TimeSeries
import no.met.observation.TimeSeriesResponseData
import play.api.libs.json.Json.toJsFieldJsValueWrapper

/**
 * Creating a json representation of Stations data
 */
object JsonTimeSeriesFormat extends BasicJsonFormat {

  implicit val TimeSeriesWriter: Writes[TimeSeries] = new Writes[TimeSeries] {

    private def withoutValue(v: JsValue): Boolean = v match {
      case JsNull => true
      case JsString("") => true
      case _ => false
    }

    def writes(timeSeries: TimeSeries): JsObject = {
      val js = Json.obj(
        "@type" -> "TimeSeries",
        "sourceId" -> timeSeries.sourceId,
        "sensorNumber" -> timeSeries.sensorNumber,
        "elementId" -> timeSeries.elementId,
        "fromDate" -> timeSeries.fromDate,
        "toDate" -> timeSeries.toDate,
        "observationTimeSpan" -> timeSeries.observationTimespan,
        "timeOffset" -> timeSeries.timeOffset
        )
      JsObject(js.fields.filterNot(t => withoutValue(t._2)))
    }
  }

  implicit val responseDataWrites: Writes[TimeSeriesResponseData] = new Writes[TimeSeriesResponseData] {
    def writes(response: TimeSeriesResponseData): JsObject = {
      header(response.header) + ("data", Json.toJson(response.data))
    }
  }

  /**
   * Create json representation of the given list
   *
   * @param start Start time of the query processing.
   * @param Stations The list to create a representation of.
   * @return json representation, as a string
   */
  def format[A](start: DateTime, timeSeries: Traversable[TimeSeries])(implicit request: Request[A]): String = {
    val size = timeSeries.size
    val duration = new Duration(DateTime.now.getMillis() - start.getMillis())
    // Create json representation
    val header = BasicResponseData("Response", "Sources", "v0", duration, size, size, size, 0, None, None)
    val response = TimeSeriesResponseData(header, timeSeries)
    Json.prettyPrint(Json.toJson(response))
  }

}
