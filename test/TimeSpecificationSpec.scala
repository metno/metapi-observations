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

import no.met.time._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import com.github.nscala_time.time.Imports._
import scala.util._

// scalastyle:off magic.number

@RunWith(classOf[JUnitRunner])
class TimeSpecificationSpec extends Specification {

  "TimeSpecification" should {
    "parse single time range" in {
      val start = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 18, 0, 0, DateTimeZone.UTC)

      val t = TimeSpecification.parse("2015-04-21T12:00:00+00/2015-04-21T18:00:00+00")
      t.get must equalTo(Seq(start to end))
    }

    "parse single time instant?" in {
      val start = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      val t = TimeSpecification.parse("2015-04-21T12:00:00+00")
      t.get must equalTo(Seq(start to start))
    }

    "parse time range with duration" in {
      val start = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 18, 0, 0, DateTimeZone.UTC)
      val t = TimeSpecification.parse("2015-04-21T12:00:00+00/PT6H")
      t.get must equalTo(Seq(start to end))
    }

    "timezones in spec" in {
      val start = new DateTime(2015, 4, 21, 10, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 16, 0, 0, DateTimeZone.UTC)
      val t = TimeSpecification.parse("2015-04-21T12:00:00+02/PT6H")
      t.get must equalTo(Seq(start to end))
    }

    "find min/max time in range" in {
      val p: String => Try[TimeSpecification.Range] = TimeSpecification.parse
      val times = p("2015-04-21T12:00:00+00/PT6H").get ++ p("2015-04-22T12:00:00+00/PT6H").get

      TimeSpecification.min(times) must equalTo(new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC))
      TimeSpecification.max(times) must equalTo(new DateTime(2015, 4, 22, 18, 0, 0, DateTimeZone.UTC))
    }

    "find min/max time in range 2" in {
      val p: String => Try[TimeSpecification.Range] = TimeSpecification.parse
      val times = p("2015-04-22T12:00:00+00/PT36H").get ++ p("2015-04-21T12:00:00+00/PT36H").get

      TimeSpecification.min(times) must equalTo(new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC))
      TimeSpecification.max(times) must equalTo(new DateTime(2015, 4, 24, 0, 0, 0, DateTimeZone.UTC))
    }

  }
}

// scalastyle:on
