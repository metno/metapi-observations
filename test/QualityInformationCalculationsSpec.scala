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

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import services.observations.QualityInformationCalculations


// scalastyle:off magic.number
@RunWith(classOf[JUnitRunner])
class QualityInformationCalculationsSpec extends Specification {
  "Quality code flag generation" should {
    "generate some data" in {
      QualityInformationCalculations.qualityCode("70000") must_== Some(0)
    }
    "generate some data" in {
      QualityInformationCalculations.qualityCode("70212") must_== Some(1)
    }
    "generate some data" in {
      QualityInformationCalculations.qualityCode("43532") must_== Some(6)
    }
    "generate some data" in {
      QualityInformationCalculations.qualityCode("43102") must_== Some(5)
    }
    "return None on invalid input" in {
      QualityInformationCalculations.qualityCode("") must_== None
    }
    "return None on invalid input" in {
      QualityInformationCalculations.qualityCode("1234") must_== None
    }
    "return None on invalid input" in {
      QualityInformationCalculations.qualityCode("123456") must_== None
    }
    "return None on invalid input" in {
      QualityInformationCalculations.qualityCode("fooba") must_== None
    }
  }
}
// scalastyle:on
