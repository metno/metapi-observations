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
}

// $COVERAGE-ON$
