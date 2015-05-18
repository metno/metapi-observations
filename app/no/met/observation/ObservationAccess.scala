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

package no.met.observation

import no.met.time._
import java.util.Date

/**
 * Interface for retrieving observation data from a data source
 */
trait ObservationAccess {

  /**
   * Retrieve a set of observations
   *
   * @param sources Locations to get data for
   * @param reftime Specification of what times to get data for
   * @param parameters Names of parameters to get data for
   *
   * @return The found observations, matching the request
   */
  @throws[Exception]("in case something went wrong")
  def observations(sources: Seq[Int], reftime: TimeSpecification.Range, parameters: Seq[String]): Seq[ObservationSeries]
}
