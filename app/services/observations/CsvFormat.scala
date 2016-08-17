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

import models.Observation
import models.ObservationSeries
import models.ObservedElement

//$COVERAGE-OFF$ Throw-away code

/**
 * Simple csv data formatting object for observation data
 */
object CsvFormat {

  /**
   * Create a header, describing the given data
   */
  def header(d: ObservationSeries): String = {

    d.observations(0).values.foldLeft("# source,\ttime")(_ + ",\t" + _.elementId)
  }

  def format(e: ObservedElement): String = {
    e.value.getOrElse("").toString()
  }
  def format(o: Observation): String = {
    o.values.foldLeft(s"${o.referenceTime}")(_ + ",\t" + format(_))
  }
  def format(d: ObservationSeries): String = {
    d.observations.foldLeft("")((x, y) => x + s"${d.sourceId},\t${format(y)}\n")
  }

  def format(d: Seq[ObservationSeries]): String = {
    d.foldLeft(header(d(0)) + "\n")(_ + _)
  }
}

//$COVERAGE-ON$
