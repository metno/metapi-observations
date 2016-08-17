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
import scala.util._
import services.observations._

// scalastyle:off magic.number

@RunWith(classOf[JUnitRunner])
class SourceSpecificationSpec extends Specification {

  "SourceSpecification" should {

    "parse single source" in {
      val s = Seq("1234")
      SourceSpecification.parse("SN1234") must equalTo(s)
      SourceSpecification.parse("SN1234 ") must equalTo(s)
      SourceSpecification.parse(" SN1234") must equalTo(s)
      SourceSpecification.parse(" SN1234 ") must equalTo(s)
      SourceSpecification.parse("SN1234,") must equalTo(s)
      SourceSpecification.parse("SN1234,,") must equalTo(s)
    }

    "parse multiple sources" in {
      val s = Seq("1234", "5678")
      SourceSpecification.parse("SN1234,SN5678") must equalTo(s)
      SourceSpecification.parse("SN1234, SN5678") must equalTo(s)
      SourceSpecification.parse("SN1234 ,SN5678") must equalTo(s)
      SourceSpecification.parse("SN1234 , SN5678") must equalTo(s)
      SourceSpecification.parse(" SN1234,SN5678 ") must equalTo(s)
      SourceSpecification.parse(" SN1234 , SN5678 ") must equalTo(s)
    }

    "parse empty" in {
      SourceSpecification.parse(",") must equalTo(Seq())
      SourceSpecification.parse(",,") must equalTo(Seq())
    }

    "throw exception" in {
      SourceSpecification.parse(", ,") must throwA[Exception]
      SourceSpecification.parse(",SN1234") must throwA[Exception]
      SourceSpecification.parse(",SN1234 ,") must throwA[Exception]
      SourceSpecification.parse(",,SN1234 ,") must throwA[Exception]
      SourceSpecification.parse(",SN1234 ,  SN5678") must throwA[Exception]
      SourceSpecification.parse(",SN1234 ,  SN5678,,") must throwA[Exception]
      SourceSpecification.parse("XX1234") must throwA[Exception]
      SourceSpecification.parse("SN 1234") must throwA[Exception]
      SourceSpecification.parse("SN") must throwA[Exception]
      SourceSpecification.parse(",SN1234") must throwA[Exception]
      SourceSpecification.parse("") must throwA[Exception]
    }
  }
}

// scalastyle:on
