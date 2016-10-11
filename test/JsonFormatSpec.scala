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
import models._
import services.observations._
import TestUtil._

@RunWith(classOf[JUnitRunner])
class JsonFormatSpec extends Specification {

  val station = "SN18700"
  //val time = DateTime.parse()
  val start = DateTime.now

  def createJsonLd(): JsValue = {
    implicit val request = FakeRequest("GET", "test")
    val data = ObservationSeries(Some(station), None, None, Some("2015-02-01T06:00:00Z"), Some(List(new Observation(Some("air_temperature"), Some(12.7), Some("degC"), None, Some("1"), Some("A"), Some(0), None))))
    val output = JsonFormat.format(start, List(data))
    Json.parse(output)
  }

  class CreateJsonLd() {
    val json = createJsonLd()
    val dataCollection = (json \ ApiConstants.DATA_NAME)(0)
    val obsData = (dataCollection \ "observations")(0)
    val valueList = (obsData \ "values")(0)
    //val temperature = valueList \ "air_temperature"
  }

  "json formatter" should {
    

    "create correctly structured output" in new WithApplication(TestUtil.app) {

      val json = createJsonLd()

      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
      ((json \ ApiConstants.DATA_NAME)(0) \ "sourceId").as[JsString] must equalTo(JsString("SN18700"))
      ((json \ ApiConstants.DATA_NAME)(0) \ "referenceTime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (((json \ ApiConstants.DATA_NAME)(0) \ "observations")(0) \ "elementId").as[JsString] must equalTo(JsString("air_temperature"))
      (((json \ ApiConstants.DATA_NAME)(0) \ "observations")(0) \ "value").as[JsNumber] must equalTo(JsNumber(12.7))
      //(((json \ ApiConstants.DATA_NAME)(0) \ "observations")(0) \ "qualityCode").as[JsString] must equalTo(JsNumber(0))
      //(((json \ ApiConstants.DATA_NAME)(0) \ "observations")(0) \ "unit").as[JsString] must equalTo(JsString("degC"))
    }

    /*
    "disable display of referencetime" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.value, Field.unit, Field.qualityCode))
      import document._

      (obsData \ "reftime").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
      (temperature \ "value").as[JsNumber] must equalTo(JsNumber(2))
      (temperature \ "qualityCode").as[JsString] must equalTo(JsString("70000"))
      (temperature \ "unit").as[JsString] must equalTo(JsString("celsius"))
    }

    "disable display of value" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.reftime, Field.unit, Field.qualityCode))
      import document._

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (temperature \ "value").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
      (temperature \ "qualityCode").as[JsString] must equalTo(JsString("70000"))
      (temperature \ "unit").as[JsString] must equalTo(JsString("celsius"))
    }

    "disable display of unit" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.reftime, Field.value, Field.qualityCode))
      import document._

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (temperature \ "value").as[JsNumber] must equalTo(JsNumber(2))
      (temperature \ "qualityCode").as[JsString] must equalTo(JsString("70000"))
      (temperature \ "unit").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
    }

    "disable display of quality" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.reftime, Field.value, Field.unit))
      import document._

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (temperature \ "value").as[JsNumber] must equalTo(JsNumber(2))
      (temperature \ "qualityCode").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
      (temperature \ "unit").as[JsString] must equalTo(JsString("celsius"))
    }

    "disable display of everything but reftime" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.reftime))
      import document._

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (valueList).validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
      (temperature).validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
    }

    "disable display of everything but quality and reftime" in new WithApplication(TestUtil.app)  {
      val document = new Doc(Set(Field.qualityCode, Field.reftime))
      import document._

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))
      (temperature \ "value").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
      (temperature \ "qualityCode").as[JsString] must equalTo(JsString("70000"))
      (temperature \ "unit").validate(Reads.optionWithNull[JsValue]) must haveClass[JsError]
    }
    */

    "contain standard headers" in new WithApplication(TestUtil.app)  {
      val document = new CreateJsonLd()
      import document._

      (json \ "@context").as[JsString] must equalTo(JsString(ApiConstants.METAPI_CONTEXT))
      (json \ "@type").as[JsString] must equalTo(JsString("ObservationResponse"))
      (json \ "apiVersion").as[JsString] must equalTo(JsString("v0"))
      (json \ "license").as[JsString] must equalTo(JsString(ApiConstants.METAPI_LICENSE))
      (json \ "totalItemCount").as[JsNumber] must equalTo(JsNumber(1))
    }

  }

}
