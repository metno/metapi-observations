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
import no.met.time._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import com.github.nscala_time.time.Imports._
import services.MockDatabaseAccess
import services.MockElementTranslator

// We are using tons of faked values for observations in this code. Turning off
// some scalastyle checking, which will run wild with this code
// scalastyle:off magic.number

@RunWith(classOf[JUnitRunner])
class KdvhObservationAccessSpec extends Specification {

  private val dummyX = 2.1
  private val dummyY = 5.7

  private val kdvhDBAccess = new MockDatabaseAccess
  private val kdvhElemTranslator = new MockElementTranslator

  "KdvhObservationAccess" should {

    "retrieve from empty time interval" in {
      val time = "2015-02-05T06:00:00Z"

      val dataSource = new KdvhObservationAccess(kdvhDBAccess, kdvhElemTranslator)
      val data = dataSource observations (List(180),
        TimeSpecification.parse(time).get,
        List("precipitation_amount", "air_temperature"),
        Set(Field.value, Field.qualityCode))

      data.size must equalTo(1)
      data(0) must equalTo(Observation.series(180, DateTime.parse(time), List("precipitation_amount" -> (dummyX, None), "air_temperature" -> (dummyY, None))))
    }

    "retrieve from multiple time intervals" in {
      val dataSource = new KdvhObservationAccess(kdvhDBAccess, kdvhElemTranslator)
      val data = dataSource observations (List(180),
        TimeSpecification.parse("R4/2015-02-01T06:00:00Z/2015-02-01T12:00:00Z/P1D").get, // four repetitions with one day repetition offset
        List("precipitation_amount", "air_temperature"),
        Set(Field.value, Field.qualityCode))

      val expectedData = List(
        ObservedElement("precipitation_amount", Some(dummyX), None),
        ObservedElement("air_temperature", Some(dummyY), None))

      data.size must equalTo(1)
      val obs = data(0).observations
      // expect four repetitions with one day repetition offset
      obs.size must equalTo(4)
      obs(0) must equalTo(Observation(DateTime.parse("2015-02-01T06:00:00Z"), expectedData))
      obs(1) must equalTo(Observation(DateTime.parse("2015-02-02T06:00:00Z"), expectedData))
      obs(2) must equalTo(Observation(DateTime.parse("2015-02-03T06:00:00Z"), expectedData))
      obs(3) must equalTo(Observation(DateTime.parse("2015-02-04T06:00:00Z"), expectedData))
    }

  }

  // scalastyle:on
}
