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
import services.DatabaseAccess

@RunWith(classOf[JUnitRunner])
class KdvhDatabaseAccessSpec extends Specification {

  "kdvh db access object" should {
    "accept valid table names" in {
      DatabaseAccess.sanitize(List("TA", "TAN_12", "rr_24")) must not(throwA[Exception])
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("\"")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("\'flf")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("a-v")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("a ")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("-ad")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List("RR 12")) must throwA[Exception]
    }
    "throw Exception on invalid table name" in {
      DatabaseAccess.sanitize(List(" TA")) must throwA[Exception]
    }
  }
}
