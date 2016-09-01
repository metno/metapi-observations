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

package services.observations

import javax.inject.Singleton
import play.Logger
import com.github.nscala_time.time.Imports._
import scala.language.postfixOps
import scala.math.BigDecimal.int2bigDecimal
import no.met.geometry.Level
import no.met.time.TimeSpecification
import no.met.time.TimeSpecification._
import models._

/**
 * Concrete implementation of KdvhDatabaseAccess class, used for development and testing.
 */
@Singleton
class MockDatabaseAccess extends DatabaseAccess {

  private val dummyX = 2.1
  private val dummyY = 5.7

  val mockDataList = List[ObservationSeries](
    new ObservationSeries(
      "SN18700",
      List[Observation](
        new Observation(
          DateTime.parse("2007-06-01T13:00:00.000Z"),
          List[ObservedElement](
            new ObservedElement(
              Some("air_temperature"),
              Some(18.7),
              Some("degC"),
              Some("7000000")
            )
          )
        )
      )
    )
  )
  
  def getObservations(elemTranslator: ElementTranslator, auth:Option[String], sources: Seq[String], refTime: TimeSpecification.Range, elements: Seq[String], withQuality: Boolean): Seq[ObservationSeries] = {
    mockDataList
    /*obsIntervals map ((obsInterval) =>
      ObservationSeries(
        stationId,
        obsInterval.start.toString(),
        Map[String, ObservedData](
          "RR_24" -> ObservedData(Option[Double](dummyX)),
          "TA" -> ObservedData(Option[Double](dummyY)))))
          * */
  }

  // scalastyle:off
  val mockTimeSerieslist = List[ObservationTimeSeries](
    new ObservationTimeSeries(Some("18700"), Some("1937-02-01T00H00M00S"), None, Some("air_temperature"), Some("PT18H"), Some("P1D"), Some("degC"), None, Seq(Level(Some("height_above_ground"), Some(2), Some("m"), None)), Some(1), Some("A"), Some("Official") ),
    new ObservationTimeSeries(Some("18700"), Some("1937-02-01T00H00M00S"), None, Some("precipitation_amount"), Some("PT18H"), Some("P1M"), Some("mm"), None, Seq(Level(Some("height_above_ground"), Some(2), Some("m"),  None)), Some(1), Some("A"), Some("Official")),
    new ObservationTimeSeries(Some("70740"), Some("1974-05-29T12H00M00S"), None, Some("air_temperature"), Some("P18H"), Some("PT6H"),Some("degC"), None, Seq(Level(Some("height_above_ground"), Some(2), Some("m"),  None)), Some(2), Some("B"), Some("Experimental"))
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

  def getTimeSeries(elemTranslator: ElementTranslator, auth: Option[String], sources: Seq[String], elements: Seq[String]): Seq[ObservationTimeSeries] = {
    mockTimeSerieslist.
      filter(s => sources.length == 0 || sources.contains(s.sourceId.get)).
      filter(s => elements.length == 0 || elements.contains(s.elementId.get))
  }

}
