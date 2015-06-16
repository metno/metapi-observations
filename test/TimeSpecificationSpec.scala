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

    "fail to parse empty spec" in {
      TimeSpecification.parse("") must beFailedTry
    }

    "parse current time" in {
      val before = DateTime.now
      Thread sleep 1000
      val now = TimeSpecification.parse("now").get.head
      Thread sleep 1000
      val after = DateTime.now
      new Interval(before, after).contains(now) must beTrue
    }

    "parse time interval" in {
      val start = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 18, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T12:00:00+00/2015-04-21T18:00:00+00").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T12:00:00/2015-04-21T18:00:00").get must equalTo(Seq(start to end))
    }

    "parse time interval with reduced precision" in {
      val start = new DateTime(2015, 1, 1, 0, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 1, 1, 6, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-01T00:00:00/2015-01T06:00:00").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-01T00:00/2015-01T06:00").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-01T00/2015-01T06").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-01/2015-01T06").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015/2015-01T06").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015/2015T06").get must equalTo(Seq(start to end))
    }

    "parse time interval with decimal fraction" in {
      val start = new DateTime(2015, 4, 21, 6, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 12, 0, 30, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T12:00,5").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T12:00.5").get must equalTo(Seq(start to end))
    }

    "parse time interval with UTC offset" in {
      val start = new DateTime(2015, 4, 21, 6, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T13:00:00+01").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T11:00:00-01").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T13:45:00+01:45").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T06:00:00/2015-04-21T13:45:00+0145").get must equalTo(Seq(start to end))
    }

    "parse time interval with 24/0 alternative" in {
      val start = new DateTime(2015, 4, 21, 0, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 27, 0, 0, 0, DateTimeZone.UTC)
      val skipReason = "hour=24 currently unsupported (but allowed in ISO 8601)"
      TimeSpecification.parse("2015-04-21T00:00/2015-04-27T00:00").get must equalTo(Seq(start to end)).orSkip(skipReason)
      TimeSpecification.parse("2015-04-21T00:00/2015-04-26T24:00").get must equalTo(Seq(start to end)).orSkip(skipReason)
      TimeSpecification.parse("2015-04-20T24:00/2015-04-27T00:00").get must equalTo(Seq(start to end)).orSkip(skipReason)
      TimeSpecification.parse("2015-04-20T24:00/2015-04-26T24:00").get must equalTo(Seq(start to end)).orSkip(skipReason)
    }

    "parse time interval with week date" in {
      val start = new DateTime(2014, 12, 28, 0, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 1, 2, 0, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2014-W52-7/2015-W01-5").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2014W527/2015W015").get must equalTo(Seq(start to end)).orSkip(
        "compact week date notation unsupported (but allowed in ISO 8601)")
    }

    "parse time interval with ordinal date" in {
      val start = new DateTime(1981, 4, 5, 0, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(1981, 4, 6, 0, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("1981-04-095/1981-04-096").get must equalTo(Seq(start to end)).orSkip(
        "ordinal dates currently unsupported (but allowed in ISO 8601)")
    }

    "fail to parse time interval without T delimiter" in {
      TimeSpecification.parse("2015-04-21 12:00:00/2015-04-21 18:00:00").get must throwA[IllegalArgumentException]
    }

    "fail to parse negative time interval" in {
      TimeSpecification.parse("2015-04-21T18:00:00/2015-04-21T12:00:00").get must throwA[IllegalArgumentException]
    }

    "parse empty time interval" in {
      val dt = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T12:00:00").get must equalTo(Seq(dt to dt))
      TimeSpecification.parse("2015-04-21T12:00:00/2015-04-21T12:00:00").get must equalTo(Seq(dt to dt))
    }

    "parse time interval with concise representation" in {
      val dt = new DateTime(2015, 2, 3, 4, 5, 6, DateTimeZone.UTC)
      val skipReason = "concise interval representation currently unsupported (but allowed in ISO 8601)"
      TimeSpecification.parse("2015-02-03T04:05:06/02-03T04:05:06").get must equalTo(Seq(dt to dt)).orSkip(skipReason)
      TimeSpecification.parse("2015-02-03T04:05:06/03T04:05:06").get must equalTo(Seq(dt to dt)).orSkip(skipReason)
      TimeSpecification.parse("2015-02-03T04:05:06/04:05:06").get must equalTo(Seq(dt to dt)).orSkip(skipReason)
      TimeSpecification.parse("2015-02-03T04:05:06/05:06").get must equalTo(Seq(dt to dt)).orSkip(skipReason)
      TimeSpecification.parse("2015-02-03T04:05:06/06").get must equalTo(Seq(dt to dt)).orSkip(skipReason)
    }

    "parse time interval with duration" in {
      val start = new DateTime(2015, 4, 21, 12, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 18, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T12:00:00/PT6H").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T12:00:00/PT0H").get must equalTo(Seq(start to start))
      TimeSpecification.parse("PT6H/2015-04-21T18:00:00").get must equalTo(Seq(start to end))
      TimeSpecification.parse("PT0H/2015-04-21T12:00:00").get must equalTo(Seq(start to start))
    }

    "parse time interval with duration and UTC offset" in {
      val start = new DateTime(2015, 4, 21, 10, 0, 0, DateTimeZone.UTC)
      val end = new DateTime(2015, 4, 21, 16, 0, 0, DateTimeZone.UTC)
      TimeSpecification.parse("2015-04-21T12:00:00+02/PT6H").get must equalTo(Seq(start to end))
      TimeSpecification.parse("2015-04-21T08:00:00-02/PT6H").get must equalTo(Seq(start to end))
    }

    "parse repeating consecutive time intervals (i.e. implicit repetition offset)" in {
      TimeSpecification.parse("R4/2005-07-01T00:00/2005-07-01T06:00").get must equalTo(Seq(
        new DateTime(2005, 7, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 12, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 12, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 18, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 1, 18, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 2, 0, 0, 0, DateTimeZone.UTC)))
    }

    // --- BEGIN ISO 8601 extension (explicit repetition offset after the Rn/<interval> form)

    "parse repeating non-consecutive time intervals (i.e. explicit repetition offset)" in {
      TimeSpecification.parse("R4/2005-07-01T00:00/2005-07-01T06:00/P1D").get must equalTo(Seq(
        new DateTime(2005, 7, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 1, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 2, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 2, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 3, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 3, 6, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 7, 4, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 7, 4, 6, 0, 0, DateTimeZone.UTC)))
      TimeSpecification.parse("R3/2004-08-01T00:00/2004-09-01T00:00/P1Y").get must equalTo(Seq(
        new DateTime(2004, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2004, 9, 1, 0, 0, 0, DateTimeZone.UTC),
        new DateTime(2005, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2005, 9, 1, 0, 0, 0, DateTimeZone.UTC),
        new DateTime(2006, 8, 1, 0, 0, 0, DateTimeZone.UTC) to new DateTime(2006, 9, 1, 0, 0, 0, DateTimeZone.UTC)))
    }

    "fail to parse missing Rn prefix" in {
      TimeSpecification.parse("2008-09-01T12:00/2008-09-01T13:00/P24H") must beFailedTry
    }

    // --- END ISO 8601 extension

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
