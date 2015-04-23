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

package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import javax.ws.rs.{ QueryParam, PathParam }
import util._
import com.wordnik.swagger.annotations._
import com.github.nscala_time.time.Imports._
import no.met.kdvh._
import no.met.observation._

// $COVERAGE-OFF$ To be tested later, when interface is more permanent

@Api(value = "/observations", description = "Access data about observations of meteorological data")
object ObservationsController extends Controller {

  /**
   * Lookup data from kdvh.
   *
   * @param stationId station number in database
   * @param fromTime Earliest time to get data for, inclusive
   * @param toTime Latest time to getdata for, exclusive
   * @param parameters comma-separated string of parameters to search for
   */
  def kdvhLookup(stationId: Int, fromTime: String, toTime: String, parameters: String) = Action { // scalastyle:ignore public.methods.have.type

    val start = DateTime.parse(fromTime)
    val end = DateTime.parse(toTime)
    val parameterList = parameters.split(",")

    DB.withConnection("kdvh") { implicit conn =>
      val kdhv = new KdvhDatabaseAccess(conn)
      val data = kdhv.getData(stationId, start to end, parameterList)
      if (data.isEmpty) {
        NotFound("Found no data for station " + stationId)
      } else {
        Ok(data.foldLeft(data(0).header)(_ + "\n" + _.toString()))
      }
    }
  }

  /**
   * Lookup data from kdvh, using data.met.no interface
   *
   * @param sources location specifications for wanted data, comma-separated
   * @param reftime time specifications
   * @param parameters Parameter names
   */
  @ApiOperation(
    nickname = "observations",
    value = "Find data for a set of stations, and a time range",
    response = classOf[String],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "An error in the request"), // scalastyle:ignore magic.number
    new ApiResponse(code = 404, message = "No data was found"))) // scalastyle:ignore magic.number
  def observations( // scalastyle:ignore public.methods.have.type
    @ApiParam(value = "Data source, comma separated", required = true)@QueryParam("sources") sources: String,
    @ApiParam(value = "Time range to get data for", required = true)@QueryParam("reftime") reftime: String,
    @ApiParam(value = "Phenomena to access", required = true)@QueryParam("parameters") parameters: String,
    @ApiParam(value = "output format", required = true, allowableValues = "json,csv",
      defaultValue = "json")@PathParam("format") format: String) = no.met.security.AuthorizedAction {

    DB.withConnection("kdvh") { implicit conn =>
      Try {
        val sourceList = sources split "," map (_.trim().toInt)
        val times = new TimeSpecification(reftime)
        val parameterList = parameters split "," map (_ trim)

        val kdvh = new KdvhDatabaseAccess(conn)
        val obsAccess = new KdvhObservationAccess(kdvh)
        obsAccess.observations(sourceList, times, parameterList)
      } match {
        case Success(data) =>
          if (data isEmpty) {
            NotFound("No data found")
          } else {
            val header = CsvFormat header data(0)
            format.toLowerCase() match {
              case "csv" => Ok(data.foldLeft(header)(_ + '\n' + CsvFormat.format(_)))
              case "json" => Ok(JsonFormat.format(data))
              case x => BadRequest(s"Invalid output format: $x")
            }
          }
        case Failure(x) => BadRequest(x getLocalizedMessage)
      }
    }

  }
}
// $COVERAGE-ON$
