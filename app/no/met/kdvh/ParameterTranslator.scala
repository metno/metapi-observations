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

/**
 * Translating application interface parameter names into kdvh parameter
 * names, and vice versa
 */
class ParameterTranslator {

  // These names are not fixed yet
  private val translations = Map(
    "precipitation_amount" -> "RR_24",
    "min_air_temperature" -> "TAN",
    "max_air_temperature" -> "TAX",
    "air_temperature" -> "TA")
  private val reverseTranslations = translations map ((entry) => (entry._2, entry._1)) toMap

  /**
   * Get a the kdvh parameter name corresponding to the given interface name.
   *
   * @return a kdvh parameter name
   */
  @throws[Exception]("if no conversion is found")
  def kdvhName(dataParameterName: String): String = {
    val ret = translations get dataParameterName
    ret getOrElse (throw new Exception("Invalid parameter name: " + dataParameterName))
  }

  /**
   * Get a the application's interface parameter name corresponding to the
   * given kdvh parameter name.
   *
   * @return a parameter name, as used by this application's interface
   */
  @throws[Exception]("if no conversion is found")
  def fromKdvhName(kdvhParameter: String): String = {
    val ret = reverseTranslations get kdvhParameter
    ret getOrElse (throw new Exception("Invalid parameter name: " + kdvhParameter))
  }

}
