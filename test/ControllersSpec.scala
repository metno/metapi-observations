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
import play.api.libs.json._
import play.api.mvc
import play.api.test._
import play.api.test.Helpers._
import no.met.data.ApiConstants

import TestUtil._

import scala.concurrent.Future

/*
 *  Note that these tests primarily exercise the routes and very basic controller
 * functionality; they are no guarantee that the queries against the database
 * will actually return correct data, as they are being run against mock data
 */
@RunWith(classOf[JUnitRunner])
class ControllersSpec extends Specification {

  "metapi /observations" should {

    // Basically just testing that the mock modules are being bound (i.e., not throwing a 500)
    "should bind the modules" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?source=SN18700&time=2015-05-01T00:00:00&element=air_temperature")).get

      status(response) must equalTo(BAD_REQUEST)
    }

    "return a result for observations with correct query parameters" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?sources=SN18700&referencetime=2007-06-01T13:00:00.000Z&elements=air_temperature")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }

    "return a result for observations with performanceCategory" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?sources=SN18700&referencetime=2007-06-01T13:00:00.000Z&elements=air_temperature&performancecategory=A")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }

    "return a result for observations with exposureCategory" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?sources=SN18700&referencetime=2007-06-01T13:00:00.000Z&elements=air_temperature&exposurecategory=1")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }
    
    "return bad request if the return format is incorrect" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.txt?sources=SN18700&referencetime=2007-06-01T13:00:00.000Z&elements=air_temperature")).get

      status(response) must equalTo(BAD_REQUEST)
    }
    
    "return a result for timeSeries with correct query parameters" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.jsonld?sources=SN18700&elements=air_temperature")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }

    "return a result for timeSeries with performancecategory" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.jsonld?sources=SN18700&elements=air_temperature&performancecategory=A")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }

    "return a result for timeSeries with exposurecategory" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.jsonld?sources=SN18700&elements=air_temperature&exposurecategory=1")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \\ ApiConstants.DATA_NAME).size must equalTo(1)
    }
    
    "return all time series with no query parameters" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.jsonld")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(3)
    }
    
    "return no data found for time series not in test set " in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.jsonld?sources=SN18701&elements=air_temperature")).get

      status(response) must equalTo(NOT_FOUND)
    }
    
    "return bad request if the return format is incorrect" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/availableTimeSeries/v0.txt?sources=SN18700&elements=air_temperature")).get

      status(response) must equalTo(BAD_REQUEST)
    }
    
  }

}
