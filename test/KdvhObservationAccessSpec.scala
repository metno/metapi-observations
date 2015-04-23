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

import no.met.observation._
import no.met.kdvh._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.specs2.mock._
import com.github.nscala_time.time.Imports._

// We are using tons of faked values for observations in this code. Turning off
// some scalastyle checking, which will run wild with this code
// scalastyle:off magic.number

@RunWith(classOf[JUnitRunner])
class KdvhObservationAccessSpec extends Specification with Mockito {

  // sample values to use
  private val x = 2.1
  private val y = 5.7

  "KdvhObservationAccess" should {

    "retrieve data" in {
      val instant = DateTime.parse("2015-02-05T06:00:00Z")
      val time = instant to instant

      val kdvh = mock[KdvhAccess]
      kdvh.getData(180, time, List("RR_24", "TA")) returns List(
        KdvhQueryResult(180, "2015-02-05T06:00:00Z",
          Map("RR_24" -> ObservedData(Some(x)), "TA" -> ObservedData(Some(y)))))
      val dataSource = new KdvhObservationAccess(kdvh)
      val data = dataSource observations (List(180), TimeSpecification("2015-02-05T06:00:00Z"), List("precipitation_amount", "air_temperature"))

      data.size must equalTo(1)
      data(0) must equalTo(Observation.series(180, instant, List("precipitation_amount" -> (x, None), "air_temperature" -> (y, None))))
    }

    "retrieve data series" in {
      val start = DateTime.parse("2015-02-01T06:00:00Z")
      val stop = DateTime.parse("2015-02-05T06:00:00Z")
      val time = start to stop

      val kdvhList = List(
        KdvhQueryResult(180, "2015-02-01T06:00:00Z", Map("TAN" -> ObservedData(Some(x)), "TAX" -> ObservedData(Some(y)))),
        KdvhQueryResult(180, "2015-02-02T06:00:00Z", Map("TAN" -> ObservedData(Some(x)), "TAX" -> ObservedData(Some(y)))),
        KdvhQueryResult(180, "2015-02-03T06:00:00Z", Map("TAN" -> ObservedData(Some(x)), "TAX" -> ObservedData(Some(y)))),
        KdvhQueryResult(180, "2015-02-04T06:00:00Z", Map("TAN" -> ObservedData(Some(x)), "TAX" -> ObservedData(Some(y)))))
      val kdvh = mock[KdvhAccess]
      kdvh.getData(180, time, List("TAN", "TAX")) returns kdvhList

      val dataSource = new KdvhObservationAccess(kdvh)
      val data = dataSource observations (List(180),
        TimeSpecification("2015-02-01T06:00:00Z/2015-02-05T06:00:00Z"),
        List("min_air_temperature", "max_air_temperature"))

      val expectedData = List(
        ObservedElement("min_air_temperature", Some(x), None),
        ObservedElement("max_air_temperature", Some(y), None))

      data.size must equalTo(1)
      val obs = data(0).observations
      obs.size must equalTo(4)
      obs(0) must equalTo(Observation(DateTime.parse("2015-02-01T06:00:00Z"), expectedData))
      obs(1) must equalTo(Observation(DateTime.parse("2015-02-02T06:00:00Z"), expectedData))
      obs(2) must equalTo(Observation(DateTime.parse("2015-02-03T06:00:00Z"), expectedData))
      obs(3) must equalTo(Observation(DateTime.parse("2015-02-04T06:00:00Z"), expectedData))
    }

  }

  // scalastyle:on
}
