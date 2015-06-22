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

package no.met.kdvh

import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent._
import ExecutionContext.Implicits.global
import javax.inject.Singleton


trait ElementTranslator {
  def kdvhName(dataElementName: String): String // ### rename to toKdvhName for consistency?
  def fromKdvhName(kdvhElement: String): String
}

// an implementation that uses the real service (instantiated in PROD mode)
@Singleton
class ElementTranslator1 extends ElementTranslator {
  def kdvhName(dataElementName: String): String = {
    "TA"
  }

  def fromKdvhName(kdvhElement: String): String = {
    "air_temperature"
  }
}

@Singleton
class ElementTranslator2 extends ElementTranslator {
  def kdvhName(dataElementName: String): String = {
    "TA"
  }

  def fromKdvhName(kdvhElement: String): String = {
    "air_temperature"
  }
}


///**
// * Translating application interface element names into kdvh element
// * names, and vice versa
// */
//class ElementTranslator {
//
//  //  // These names are not fixed yet
//  //  private val translations = Map(
//  //    "precipitation_amount" -> "RR_24",
//  //    "min_air_temperature" -> "TAN",
//  //    "max_air_temperature" -> "TAX",
//  //    "air_temperature" -> "TA")
//  //  private val reverseTranslations = translations map ((entry) => (entry._2, entry._1)) toMap // ### is toMap needed here?
//
//  /**
//   * Get the kdvh element name corresponding to the given interface element name.
//   *
//   * @return a kdvh element name
//   */
//  @throws[Exception]("if no conversion is found")
//  def kdvhName(dataElementName: String): String = {
//    //    val ret = translations get dataElementName
//    //    ret getOrElse (throw new Exception("Invalid element name: " + dataElementName))
//
//    //    Configure: SERVICE = https://data.met.no/elements (or http://localhost:9999)
//    //
//    //    SERVICE/elements?ids=air_temperature
//    //
//    //    SERVICE/elements/air_temperature
//    //
//    //    SERVICE/elements?ids=air_temperature,precipitation_amount
//    //
//    //    JSON -> TA
//
//    //      wget -O - "http://eklima.met.no/metdata/MetDataService?invoke=getTimeserieTypesProperties&language=&timeserieTypes=" skal gi fornuftig XML output
//    //      som kan brukes for testing
//
//    val request: WSRequestHolder = WS.url("http://eklima.met.no/metdata/MetDataService")
//      .withQueryString(
//        ("insvoke", "getTimeserieTypesProperties"),
//        ("language", ""),
//        ("timeserieTypes", ""))
//
//    val response: Future[WSResponse] = request.get()
//
//    response onComplete {
//      case Success(x) => println("success... x.body: " + x.body)
//      case Failure(x) => println("failure: " + x.getMessage)
//    }
//
//    s"kdvhName-for-$dataElementName-=-dummy"
//    //"TA"
//
//  }
//
//  /**
//   * Get a the application's interface element name corresponding to the
//   * given kdvh element name.
//   *
//   * @return an element name, as used by this application's interface
//   */
//  @throws[Exception]("if no conversion is found")
//  def fromKdvhName(kdvhElement: String): String = {
//    //    val ret = reverseTranslations get kdvhElement
//    //    ret getOrElse (throw new Exception("Invalid element name: " + kdvhElement))
//
//    //    https://data.met.no/elements?kdvhCode=TA
//    //
//    //    JSON -> air_temperature
//
//    s"fromKdvhName-for-$kdvhElement-=-dummy"
//
//  }
//
//}
