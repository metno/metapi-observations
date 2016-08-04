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

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.Play.current
import play.api.libs.json._
import play.api.test.Helpers._
import com.github.nscala_time.time.Imports._
import no.met.observation._
import services.observations.JsonFormat
import TestUtil._

@RunWith(classOf[JUnitRunner])
class JsonFormatSpec extends Specification {

  val station = 180
  val time = DateTime.parse("2015-02-01T06:00:00Z")
  val start = DateTime.now

  def doc(fields: Set[Field.Field] = Field.default): JsValue = {
    implicit val request = FakeRequest("GET", "test")
    val data = Observation.series(station, time, Map("air_temperature" -> (2, Some("70000"))))
    val output = new JsonFormat(fields).format(start, List(data))
    Json.parse(output)
  }

  class Doc(fields: Set[Field.Field] = Field.default) {
    val json = doc(fields)
    val dataCollection = (json \ "data")(0)
    val obsData = (dataCollection \ "dataSet")(0)
    val valueList = (obsData \ "values")(0)
    val temperature = valueList \ "air_temperature"
  }

  "json formatter" should {

    "create some output" in new WithApplication(TestUtil.app) {
    //running(FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase("kdvh"))) {

      val document = new Doc()
      import document._

      (json \\ "data").size must equalTo(1)

      (dataCollection \ "@type").as[JsString] must equalTo(JsString("DataCollection"))
      (dataCollection \ "source").as[JsString] must equalTo(JsString("KS180"))
      (dataCollection \ "level").as[JsString] must equalTo(JsString("ground_level"))
      (dataCollection \ "geometry" \ "type").as[JsString] must equalTo(JsString("Point"))
      (dataCollection \ "geometry" \ "coordinates").as[JsValue] must equalTo(JsArray(Seq(JsNumber(9.0), JsNumber(62.3))))

      (obsData \ "reftime").as[JsString] must equalTo(JsString("2015-02-01T06:00:00Z"))

      (temperature \ "value").as[JsNumber] must equalTo(JsNumber(2))
      (temperature \ "qualityCode").as[JsString] must equalTo(JsString("70000"))
      (temperature \ "unit").as[JsString] must equalTo(JsString("celsius"))
    }

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

    "contain standard headers" in new WithApplication(TestUtil.app)  {
      val document = new Doc()
      import document._

      (json \ "@context").as[JsString] must equalTo(JsString("https://data.met.no/schema/"))
      (json \ "@type").as[JsString] must equalTo(JsString("Response"))
      (json \ "@id").as[JsString] must equalTo(JsString("Observations"))
      (json \ "apiVersion").as[JsString] must equalTo(JsString("v0"))
      (json \ "license").as[JsString] must equalTo(JsString("http://met.no/English/Data_Policy_and_Data_Services/"))
      //(json \ "createdAt")
      //(json \ "queryTime")
      (json \ "totalItemCount").as[JsNumber] must equalTo(JsNumber(1))
      //(json \ "currentLink") must equalTo(JsString("http://localhost:9000/test")) // This is only valid if server name and stuff is left unconfigured
    }

  }
}
