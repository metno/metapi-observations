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

import java.util.Date
import no.met.time._
import no.met.kdvh._
import com.github.nscala_time.time.Imports._
import play.api.Logger

/**
 * Interface for retrieving observation data from the kdvh database
 */
class KdvhObservationAccess(val kdvh: KdvhAccess) extends ObservationAccess {

  private val translator = new ParameterTranslator

  /**
   * Get observation data from a single source
   */
  private def observations(source: Int, reftime: TimeSpecification.Range, parameters: Seq[String], fields: Set[Field.Value]): Seq[Observation] = {

    val kdvhParameters: Seq[String] = parameters map (translator.kdvhName(_))

    val databaseResult = kdvh.getData(source, reftime, kdvhParameters, fields.contains(Field.quality))

    // If you run a test, and get a NullPointerException, it is because you
    // have made an error when setting up KdvhAccess mocking object, so the
    // call you make is not accounted for
    databaseResult map {
      (r: KdvhQueryResult) =>
        {
          val time = DateTime.parse(r.date)
          val data = r.parameter.map { (v) =>
            ObservedElement(translator.fromKdvhName(v._1), v._2.value, v._2.quality)
          }
          Observation(time, data)
        }
    }
  }

  override def observations(sources: Seq[Int], reftime: TimeSpecification.Range, parameters: Seq[String], fields: Set[Field.Value]): Seq[ObservationSeries] = {
    sources.map((source) => ObservationSeries(source, observations(source, reftime, parameters, fields)))
  }
}
