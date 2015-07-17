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
import services._

//$COVERAGE-OFF$Not testing database queries

/**
 * Concrete implementation of KdvhElementTranslator class, used for development and testing.
 */
@Singleton
class MockElementTranslator extends ElementTranslator {
  // These names are not fixed yet
  private val translations = Map(
    "precipitation_amount" -> "RR_24",
    "min_air_temperature" -> "TAN",
    "max_air_temperature" -> "TAX",
    "air_temperature" -> "TA")
  private val reverseTranslations = translations map ((entry) => (entry._2, entry._1))

  override def toKdvhElemName(auth:Option[String], apiElemName: String): String = {
    val ret = translations get apiElemName
    ret getOrElse (throw new Exception("Invalid API element name: " + apiElemName))
  }

  override def toApiElemName(auth:Option[String], kdvhElemName: String): String = {
    val ret = reverseTranslations get kdvhElemName
    ret getOrElse (throw new Exception("Invalid KDVH element name: " + kdvhElemName))
  }
}

// $COVERAGE-ON$
