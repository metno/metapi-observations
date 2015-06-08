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

    // ISO-8601 extensions with repeating intervals which do not exist in the ISO standard

    "parse current time" in {
      val before = DateTime.now
      Thread sleep 1000
      val now = TimeSpecification.parse("now").get.head
      Thread sleep 1000
      val after = DateTime.now
      new Interval(before, after).contains(now) must beTrue
    }

    "parse repeating consecutive time intervals" in {
      val dates = Seq(
        new DateTime(2005, 7, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 12, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 12, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 18, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 18, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 2, 0, 0, 0, DateTimeZone.UTC))
      val t = TimeSpecification.parse("R4/2005-07-01T00:00/2005-07-01T06:00")
      t.get must equalTo(dates)
    }

    "parse repeating time intervals with duration" in {
      val dates = Seq(
        new DateTime(2005, 7, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 2, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 2, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 3, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 3, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 4, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 4, 6, 0, 0, DateTimeZone.UTC))
      val t = TimeSpecification.parse("R4/2005-07-01T00:00/2005-07-01T06:00/P1D")
      t.get must equalTo(dates)
    }

    //The following query retrieves data for august and september only, for three years (2004 - 2006).
    // R3/2004-08-01T00:00/2004-10-01T00:00/P1Y

    "parse repeating time ranges with duration" in {
      val dates = Seq(
        new DateTime(2004, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2004, 9, 1, 0, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 9, 1, 0, 0, 0, DateTimeZone.UTC),
        new DateTime(2006, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2006, 9, 1, 0, 0, 0, DateTimeZone.UTC))
      val t = TimeSpecification.parse("R3/2004-08-01T00:00/2004-09-01T00:00/P1Y")
      t.get must equalTo(dates)
    }

    "illegal format" in {
      TimeSpecification.parse("2008-09-01T12:00/2008-09-01T13:00/P24H") must beFailedTry
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
