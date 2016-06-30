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

package services

import javax.inject.Singleton
import com.github.nscala_time.time.Imports._
import no.met.observation._
import services._
import no.met.kdvh.KdvhQueryResult
import no.met.kdvh.ObservedData
import scala.math.BigDecimal.int2bigDecimal

//$COVERAGE-OFF$Not testing database queries

/**
 * Concrete implementation of KdvhDatabaseAccess class, used for development and testing.
 */
@Singleton
class MockDatabaseAccess extends DatabaseAccess {

  private val dummyX = 2.1
  private val dummyY = 5.7

  def getData(stationId: Int, obsIntervals: Seq[Interval], elements: Seq[String], withQuality: Boolean): Seq[KdvhQueryResult] = {
    obsIntervals map ((obsInterval) =>
      KdvhQueryResult(
        stationId,
        obsInterval.start.toString(),
        Map[String, ObservedData](
          "RR_24" -> ObservedData(Option[Double](dummyX)),
          "TA" -> ObservedData(Option[Double](dummyY)))))
  }

    // scalastyle:off
  val mockSourcelist = List[TimeSeries](
    new TimeSeries(18700, 1, "air_temperature", "01-FEB-37", None, "1M", "18UTC")
    /*
    new Station("KN18700",   "OSLO - BLINDERN",      "Norge",               Some(1492),  Some(94),  Some(59.9423),          Some(10.72),              "1941-01-01"),
    new Station("KN70740",   "STEINKJER",            "Norge",               None,        Some(10),  Some(64.02),            Some(11.5),               "1500-01-01"),
    new Station("KN76931",   "TROLL A",              "Norge",               Some(1309),  Some(128), Some(60.6435),          Some(3.7193),             "2010-12-01"),
    new Station("KN377200",  "HEATHROW",             "Storbritannia",       Some(3772),  Some(24),  Some(51.4791666666667), Some(-0.450546448087432), "2015-02-03"),
    new Station("KN401800",  "KEFLAVIKURFLUGVOLLUR", "Island",              Some(4018),  Some(52),  Some(63.9805555555556), Some(-22.5948087431694),  "2015-02-03"),
    new Station("KN2647700", "VELIKIE LUKI",         "Russland (i Europa)", Some(26477), Some(97),  Some(56.35),            Some(30.6166666666667),   "2011-08-14"),
    new Station("KN4794600", "OKINAWA",              "Japan",               Some(47946), None,      Some(26.5),             Some(127.9),              "2013-06-01")
    */
  )
  // scalastyle:on

  def getTimeSeries(stationIds: Seq[Int], elements: Seq[String]): List[TimeSeries] = {
    mockSourcelist.
      filter(s => stationIds.length == 0 || stationIds.contains(s.sourceId)).
      filter(s => elements.length == 0    || elements.contains(s.elementId))
  }

}

// $COVERAGE-ON$
