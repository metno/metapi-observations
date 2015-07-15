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
import play.api.mvc._
import javax.inject.Inject
import javax.ws.rs.{ QueryParam, PathParam }
import util._
import com.wordnik.swagger.annotations._
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports._
import no.met.kdvh._
import no.met.observation._
import no.met.observation.Field._
import no.met.time._
import services.DatabaseAccess
import services.ElementTranslator

// $COVERAGE-OFF$ To be tested later, when interface is more permanent

@Api(value = "/observations", description = "Access data about observations of meteorological data")
class ObservationsController @Inject()(kdvhDBAccess: DatabaseAccess, kdvhElemTranslator: ElementTranslator) extends Controller {

  /**
   * Lookup data from kdvh.
   *
   * @param stationId station number in database
   * @param fromTime Earliest time to get data for, inclusive
   * @param toTime Latest time to getdata for, exclusive
   * @param elements comma-separated string of elements to search for
   */
  def kdvhLookup(stationId: Int, fromTime: String, toTime: String, elements: String) = Action { // scalastyle:ignore public.methods.have.type

    val start = DateTime.parse(fromTime)
    val end = DateTime.parse(toTime)
    val elementList = elements.split(",")

    val data = kdvhDBAccess.getData(stationId, List(start to end), elementList, false)
    if (data.isEmpty) {
      NotFound("No data found for station " + stationId)
    } else {
      Ok(data.foldLeft(data(0).header)(_ + "\n" + _.toString()))
    }
  }

  def fieldSet(fields: Option[String]): Set[Field] = fields match {
    case None     => Field.default
    case Some("") => Field.default
    case Some(field) => {
      field split "," map { f =>
        try {
          Field withName f.trim()
        } catch {
          case x: NoSuchElementException => throw new NoSuchElementException(s"$f is not a valid field")
        }
      } toSet
    }
  }

  /**
   * Lookup data from kdvh, using data.met.no interface
   *
   * @param sources location specifications for wanted data, comma-separated
   * @param reftime time specifications
   * @param elements Element names
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
    @ApiParam(value = "Phenomena to access", required = true)@QueryParam("elements") elements: String,
    @ApiParam(value = "Fields to access", required = false, allowableValues = "value,unit,qualityCode")
      @QueryParam("fields") fields: Option[String],
    @ApiParam(value = "output format", required = true, allowableValues = "jsonld,csv",
      defaultValue = "jsonld")@PathParam("format") format: String) = no.met.security.AuthorizedAction {
    implicit request =>

    val start = DateTime.now(DateTimeZone.UTC)

    var fieldList = Set.empty[Field]

    Try {
      val sourceList = SourceSpecification.parse(sources)
      val times = TimeSpecification.parse(reftime).get
      val elementList = elements split "," map (_ trim)
      fieldList = fieldSet(fields)
      val obsAccess = new KdvhObservationAccess(kdvhDBAccess, kdvhElemTranslator)
      obsAccess.observations(sourceList, times, elementList, fieldList)
    } match {
      case Success(data) =>
        if (data isEmpty) {
          NotFound("No data found")
        } else {
          format.toLowerCase() match {
            case "csv"    => Ok(data.foldLeft(CsvFormat header data(0))(_ + '\n' + CsvFormat.format(_))) as "text/csv"
            case "jsonld" => Ok(new JsonFormatter(fieldList).format(start, data)) as "application/vnd.no.met.data.observations-v0+json"
            case x        => BadRequest(s"Invalid output format: $x")
          }
        }
      case Failure(x) => BadRequest(x getLocalizedMessage)
    }
  }
}
// $COVERAGE-ON$