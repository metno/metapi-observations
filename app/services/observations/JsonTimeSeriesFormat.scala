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
import play.api.libs.functional.syntax._
import com.github.nscala_time.time.Imports._
import java.net.URL
import no.met.data.{ApiConstants,BasicResponseData,ConfigUtil}
import no.met.data.format.json.BasicJsonFormat
import models._

//$COVERAGE-OFF$ 
/**
 * Creating a json representation of Stations data
 */
object JsonTimeSeriesFormat extends BasicJsonFormat {

  implicit val observationTimeSeriesWrites: Writes[ObservationTimeSeries] = (
    (JsPath \ "sourceId").write[String] and 
    (JsPath \ "sensorNumber").writeNullable[Int] and 
    (JsPath \ "fromDate").write[String] and 
    (JsPath \ "toDate").writeNullable[String] and 
    (JsPath \ "elementId").writeNullable[String] and 
    (JsPath \ "observationTimeSpan").write[String] and 
    (JsPath \ "timeOffset").write[String]
  )(unlift(ObservationTimeSeries.unapply))

  implicit val observationTimeSeriesResponseWrites: Writes[ObservationTimeSeriesResponse] = (
    (JsPath \ ApiConstants.CONTEXT_NAME).write[URL] and 
    (JsPath \ ApiConstants.OBJECT_TYPE_NAME).write[String] and
    (JsPath \ ApiConstants.API_VERSION_NAME).write[String] and
    (JsPath \ ApiConstants.LICENSE_NAME).write[URL] and
    (JsPath \ ApiConstants.CREATED_AT_NAME).write[DateTime] and
    (JsPath \ ApiConstants.QUERY_TIME_NAME).write[Duration] and
    (JsPath \ ApiConstants.CURRENT_ITEM_COUNT_NAME).write[Long] and
    (JsPath \ ApiConstants.ITEMS_PER_PAGE_NAME).write[Long] and
    (JsPath \ ApiConstants.OFFSET_NAME).write[Long] and
    (JsPath \ ApiConstants.TOTAL_ITEM_COUNT_NAME).write[Long] and
    (JsPath \ ApiConstants.NEXT_LINK_NAME).writeNullable[URL] and
    (JsPath \ ApiConstants.PREVIOUS_LINK_NAME).writeNullable[URL] and
    (JsPath \ ApiConstants.CURRENT_LINK_NAME).write[URL] and
    (JsPath \ ApiConstants.DATA_NAME).write[Seq[ObservationTimeSeries]]
  )(unlift(ObservationTimeSeriesResponse.unapply))

  /**
   * Create json representation of the given list
   * @param start Start time of the query processing.
   * @param observations The list to create a representation of.
   * @return json representation, as a string
   */
  def format[A](start: DateTime, timeSeries: Seq[ObservationTimeSeries])(implicit request: Request[A]): String = {
    val size = timeSeries.size // TODO: dataSize(observations)
    val duration = new Duration(DateTime.now.getMillis() - start.getMillis())
    val response = new ObservationTimeSeriesResponse( new URL(ApiConstants.METAPI_CONTEXT),
                                       "ObservationTimeSeriesResponse",
                                       "v0",
                                        new URL(ApiConstants.METAPI_LICENSE),
                                        start,
                                        duration,
                                        size,
                                        size,
                                        0,
                                        size,
                                        None,
                                        None,
                                        new URL(ConfigUtil.urlStart + request.uri),
                                        timeSeries)
    Json.prettyPrint(Json.toJson(response))
  }
  
}
//$COVERAGE-ON$ 