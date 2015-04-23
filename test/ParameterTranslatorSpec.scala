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
import no.met.kdvh._


@RunWith(classOf[JUnitRunner])
class ParameterTranslatorSpec extends Specification {

  val translator = new ParameterTranslator

  "Parameter translator" should {

    "return translated data" in {
      translator kdvhName "air_temperature" must equalTo("TA")
    }

    "do reverse translations" in {
      translator fromKdvhName "TA" must equalTo("air_temperature")
    }

    "throw exception on translation of unknown parameter" in {
      translator kdvhName "no_such_parameter" must throwA[Exception]
    }

    "throw exception on reverse translation of unknown parameter" in {
      translator fromKdvhName "NOTHING" must throwA[Exception]
    }

  }
}
