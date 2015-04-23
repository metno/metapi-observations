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

package test

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import no.met.observation._
import com.github.nscala_time.time.Imports._
import play.api.libs.json._

@RunWith(classOf[JUnitRunner])
class JsonFormatSpec extends Specification {

  val station = 180
  val time = DateTime.parse("2015-02-01T06:00:00Z")

  "json formatter" should {

    "create some output" in {

      val data = Observation.series(station, time, Map("air_temperature" -> (2, Some("70000"))))
      val output = JsonFormat.format(List(data))

      val fullJson = Json.parse(output)
      fullJson.as[JsArray].value.size must equalTo(1)

      val json = fullJson(0)
      (json \\ "observations").size must equalTo(1)
      ((json \ "observations")(0) \\ "data").size must equalTo(1)

      val obsData = ((json \ "observations")(0) \ "data")(0)
      val temperature = obsData \ "air_temperature"

      (json \ "source") must equalTo(JsNumber(station))
      temperature \ "value" must equalTo(JsNumber(2))
      temperature \ "quality" must equalTo(JsString("70000"))
    }

  }

}
