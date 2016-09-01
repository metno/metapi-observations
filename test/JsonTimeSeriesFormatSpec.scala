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

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._
import play.api.Play.current
import play.api.libs.json._
import play.api.test.Helpers._
import com.github.nscala_time.time.Imports._
import no.met.data.ApiConstants
import no.met.geometry.Level
import models._
import services.observations._
import TestUtil._

@RunWith(classOf[JUnitRunner])
class JsonTimeSeriesFormatSpec extends Specification {

  val station = "SN18700"
  val time = "2015-02-01T06:00:00Z"
  val start = DateTime.now

  def createJsonLd() : JsValue = {
    implicit val request = FakeRequest("GET", "test")
    val data =  ObservationTimeSeries(Some("SN18700"), Some("1937-02-01T00H00M00S"), None, Some("air_temperature"), Some("PT18H"), Some("P1D"), Some("degC"), None, Seq(Level(Some("height_above_ground"), Some(2), Some("m"), None)), Some(1), Some("A"), Some("Official") )
    val output = JsonTimeSeriesFormat.format(start, List(data))
    Json.parse(output)
  }

  class CreateJsonLd() {
    val json = createJsonLd()
    val dataCollection = (json \ ApiConstants.DATA_NAME)(0)
    val obsData = (dataCollection \ "observations")(0)
    val valueList = (obsData \ "values")(0)
    //val temperature = valueList \ "air_temperature"
  }

  "json time series formatter" should {

    "create correctly structured output" in new WithApplication(TestUtil.app) {

      val json = createJsonLd()

      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
      ((json \ ApiConstants.DATA_NAME)(0) \ "sourceId").as[JsString] must equalTo(JsString("SN18700"))
      ((json \ ApiConstants.DATA_NAME)(0) \ "validFrom").as[JsString] must equalTo(JsString("1937-02-01T00H00M00S"))
      ((json \ ApiConstants.DATA_NAME)(0) \ "elementId").as[JsString] must equalTo(JsString("air_temperature"))
      ((json \ ApiConstants.DATA_NAME)(0) \ "offset").as[JsString] must equalTo(JsString("PT18H"))
      ((json \ ApiConstants.DATA_NAME)(0) \ "resultTimeInterval").as[JsString] must equalTo(JsString("P1D"))
    }

    "not return toDate if station is still valid" in new WithApplication(TestUtil.app) {

      val json = createJsonLd()

      ((json \ ApiConstants.DATA_NAME)(0) \ "toDate").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
    }

    "contain standard headers" in new WithApplication(TestUtil.app)  {
      val document = new CreateJsonLd()
      import document._

      (json \ "@context").as[JsString] must equalTo(JsString(ApiConstants.METAPI_CONTEXT))
      (json \ "@type").as[JsString] must equalTo(JsString("ObservationTimeSeriesResponse"))
      (json \ "apiVersion").as[JsString] must equalTo(JsString("v0"))
      (json \ "license").as[JsString] must equalTo(JsString(ApiConstants.METAPI_LICENSE))
      (json \ "totalItemCount").as[JsNumber] must equalTo(JsNumber(1))
    }

  }

}
