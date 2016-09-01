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

package services.observations

import scala.language.postfixOps
import scala.util._

/**
 * Parsing of sources.
 */
object SourceSpecification {

  /**
   * Attempts to extract a list of climate station numbers from a string.
   * @param sources A list of one or more climate station numbers prefixed with "SN", e.g. "SN1234, SN4567".
   */
  def parse(sources: String): Seq[String] = {

    /** Returns the integer resulting from removing a prefix from a string.
     * @param s Input string, expected to include the prefix.
     * @param prefix Prefix, expected to be a combination of characters from [a-z] and [A-Z].
     *   Special characters are not guaranteed to work (in particular not '(' and ')').
     */
    def stripPrefixFromInt(s: String, prefix: String): String = {
      val pattern = s"""$prefix(\\d+)""".r
      s match {
        case pattern(x) => x
        case _ => throw new no.met.data.BadRequestException(s"Invalid source name: $s (expected $prefix<int>)",
          Some(s"Currently, all sources must have the prefix $prefix, like this: ${prefix}18700"))
      }
    }

    sources split "," map (s => stripPrefixFromInt(s.trim().toString, "SN")) toSeq
    
  }
}
