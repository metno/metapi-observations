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

import no.met.kdvh._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

// We are using tons of faked values for observations in this code. Turning off
// some scalastyle checking, which will run wild with this code
// scalastyle:off magic.number

@RunWith(classOf[JUnitRunner])
class KdvhQueryResultSpec extends Specification {

  "A query result object" should {
    "be possible to construct simply" in {
      new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)), "TA" -> ObservedData(Some(18.3))))
      true
    }

    "not be mergeable with other query results with different stationid" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map())
      val b = new KdvhQueryResult(181, "2015-02-26T06:00:00Z", Map())
      a.mergedWith(b) must throwA[Exception]
    }

    "not be mergeable with other query results with differing times" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)), "TA" -> ObservedData(Some(18.3))))
      val b = new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)), "TA" -> ObservedData(Some(18.3))))
      a.mergedWith(b) must throwA[Exception]
    }

    "be mergeable with other query results with same time" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("TA" -> ObservedData(Some(18.3))))
      val c = a mergedWith b
      true
    }

    "collate parameter from both sets when merging" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("TA" -> ObservedData(Some(18.3))))
      val c = a mergedWith b
      c.parameter must havePairs("RR_12" -> ObservedData(Some(2.1)), "TA" -> ObservedData(Some(18.3)))
    }

    "prefer first argument when merging and parameters are conflicting" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2))))
      val c = a mergedWith b
      c.parameter must havePair("RR_12" -> ObservedData(Some(2.1)))
    }

    "perserve quality information when merging" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1), Some("70000"))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("TA" -> ObservedData(Some(18.3), Some("99900"))))
      val c = a mergedWith b
      c.parameter must havePairs("RR_12" -> ObservedData(Some(2.1), Some("70000")), "TA" -> ObservedData(Some(18.3), Some("99900")))
    }

    "prefer first argument when merging and parameters are conflicting" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1), Some("123456"))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2), Some("654321"))))
      val c = a mergedWith b
      c.parameter must havePair("RR_12" -> ObservedData(Some(2.1), Some("123456")))
    }

    "prefer first argument when merging and parameters are conflicting" in {
      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1), Some("123456"))))
      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2), Some("654321"))))
      val c = b mergedWith a // Turned around
      c.parameter must havePair("RR_12" -> ObservedData(Some(3.2), Some("654321")))
    }

    // Unsure of this one - it means data sets may be mixed if a station
    // reports in one type RR_12 every hour, but only every 12 hours in the
    // preferred type:
    //    "prefer second parameter value when first is None" in {
    //      val a = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> None))
    //      val b = new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2))))
    //      val c = a mergedWith b
    //      c(0).parameter must havePair("RR_12" -> ObservedData(Some(3.2)))
    //    }

    "two merged lists must not contain date duplicates" in {
      val a = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2)))))
      val b = List(
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)))))
      val c = KdvhQueryResult.merge(a, b)
      c.length must beEqualTo(2)
    }

    "two merged lists must contain all elements" in {
      val a = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2)))))
      val b = List(
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(2.1)))),
        new KdvhQueryResult(180, "2015-02-28T06:00:00Z", Map("RR_12" -> ObservedData(Some(3.2)))))
      val c = KdvhQueryResult.merge(a, b)
      c.length must beEqualTo(3)
    }

    "prefer first values when merging lists" in {
      val a = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(1)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(2)))))
      val b = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(4)))))
      val c = KdvhQueryResult.merge(a, b)
      c(0).parameter must havePair("RR_12" -> ObservedData(Some(1)))
    }
    "prefer first values when merging lists" in {
      val a = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(1)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(2)))))
      val b = List(
        new KdvhQueryResult(180, "2015-02-26T06:00:00Z", Map("RR_12" -> ObservedData(Some(3)))),
        new KdvhQueryResult(180, "2015-02-27T06:00:00Z", Map("RR_12" -> ObservedData(Some(4)))))
      val c = KdvhQueryResult.merge(a, b)
      c(1).parameter must havePair("RR_12" -> ObservedData(Some(2)))
    }
  }

  // scalastyle:on
}
