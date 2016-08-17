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

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import services.observations._

@RunWith(classOf[JUnitRunner])
class KdvhElementTranslatorSpec extends Specification {

  val kdvhElemTranslatorNonProd = new MockElementTranslator

  "The mock ElementTranslator service" should {

    "return translated data for a single value" in {
      kdvhElemTranslatorNonProd.toKdvhElemName(None, "precipitation_amount") must equalTo(Seq("RR_24"))
    }

    "return translated data for a sequence value" in {
      kdvhElemTranslatorNonProd.toKdvhElemName(None, "air_temperature") must equalTo(Seq("TA", "TA10"))
    }
    
    "do reverse translations" in {
      kdvhElemTranslatorNonProd.toApiElemName(None, "TA") must equalTo(Some("air_temperature"))
    }

    "throw exception on translation of unknown element" in {
      kdvhElemTranslatorNonProd.toKdvhElemName(None, "no_such_element") must throwA[Exception]
    }

    "return None on reverse translation of unknown element" in {
      kdvhElemTranslatorNonProd.toApiElemName(None, "NOTHING") must equalTo(None)
    }
  }
}
