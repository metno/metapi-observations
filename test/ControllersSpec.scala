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
import play.api.mvc
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
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
      val response = route(FakeRequest(GET, "/v0.jsonld?source=KN18700&time=2015-05-01T00:00:00&element=air_temperature")).get

      status(response) must equalTo(BAD_REQUEST)
    }

  }

}
