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
import com.github.nscala_time.time.Imports._
import io.swagger.annotations._
import javax.inject.Inject
import scala.language.postfixOps
import util._
import no.met.time.TimeSpecification
import services.observations._

//$COVERAGE-OFF$ 

@Api(value = "/observations")
class ObservationsController @Inject()(dataAccess: DatabaseAccess, elemTranslator: ElementTranslator) extends Controller {

  /*
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
  * 
  */

  /**
   * GET observations data from the KDVH database
   */
  @ApiOperation(
    value = "Get observation data from the MET API.",
    notes = "This is the core resource for retrieving the actual observation data from MET Norway's data storage systems. The query parameters act as a filter; if all were left blank (not allowed in practice), one would retrieve all of the observation data in the system. Restrict the data using the query parameters. For possible input parameters see /sources, /elements, and /observations/timeseries.",
    response = classOf[models.ObservationResponse],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid parameter value or malformed request."), // scalastyle:ignore magic.number
    new ApiResponse(code = 401, message = "Unauthorized client ID."), // scalastyle:ignore magic.number
    new ApiResponse(code = 404, message = "No data was found for the list of query Ids."), // scalastyle:ignore magic.number
    new ApiResponse(code = 500, message = "Internal server error."))) // scalastyle:ignore magic.number
  def observations( // scalastyle:ignore public.methods.have.type
    @ApiParam(value = "The ID(s) of the data sources that you want observations from. Enter a comma-separated list to retrieve data from multiple sources. To retrieve a station, use the MET API station ID; e.g., _SN18700_ for Blindern. Retrieve the complete station lists using the <a href=\"https://data.met.no/docs#/sources\">sources</a> resource.",
              required = true)
              sources: String,
    @ApiParam(value = "The time range that you want observations for. Time ranges are specified in an extended ISO-8601 format; see the referebce section on <a href=\"https://data.met.no/references.html#time_specification\">Time Specification</a> for documentation and examples.",
              required = true)
              reftime: String,
    @ApiParam(value = "The elements that you want observations for. Enter a comma-separated list to retrieve data for multiple elements. Elements follow the MET API naming convention and a complete list of all elements in the system can be retrieves using the <a href=\"https://data.met.no/docs#/elements\">elements</a> resource.",
              required = true)
              elements: String,
    @ApiParam(value = "Fields to access",
              required = false,
              allowableValues = "value,unit,qualityCode")
              fields: Option[String],
    @ApiParam(value = "The output format of the result.",
              required = true,
              allowableValues = "jsonld,csv",
              defaultValue = "jsonld")
              format: String) = no.met.security.AuthorizedAction {
    implicit request =>

    val start = DateTime.now(DateTimeZone.UTC)

    //var fieldList = Set.empty[Field]

    Try {
      val auth = request.headers.get("Authorization")
      Logger.debug("Authorization: " + auth)
      val sourceDef = SourceSpecification.parse(sources)
      val timeDef = TimeSpecification.parse(reftime).get
      val elementDef = elements split "," map (_ trim)
      dataAccess.getObservations(elemTranslator, auth, sourceDef, timeDef, elementDef, true); 
      /*
      DatabaseAccess.sanitize(elements)
       * 
       * fieldList = fieldSet(fields)
      val obsAccess = new KdvhDBAccess.observations(kdvhDBAccess, kdvhElemTranslator)
      obsAccess.observations(auth, sourceList, times, elementList, fieldList)*/
    } match {
      case Success(data) =>
        if (data isEmpty) {
          NotFound("No data found")
        } else {
          format.toLowerCase() match {
            case "csv"    => Ok(data.foldLeft(CsvFormat header data(0))(_ + '\n' + CsvFormat.format(_))) as "text/csv"
            case "jsonld" => Ok(JsonFormat.format(start, data)) as "application/vnd.no.met.data.observations-v0+json"
            case x        => BadRequest(s"Invalid output format: $x")
          }
        }
      case Failure(x) => BadRequest(x getLocalizedMessage)
    }
  }

  /**
   * Lookup timeseries metadata from kdvh, using data.met.no interface
   *
   * @param sources location specifications for wanted data, comma-separated
   * @param reftime time specifications
   * @param elements Element names
   */
  @ApiOperation(
    nickname = "timeSeries",
    value = "Find timeseries metadata by source and/or element",
    response = classOf[models.ObservationTimeSeriesResponse],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid parameter value or malformed request."), // scalastyle:ignore magic.number
    new ApiResponse(code = 401, message = "Unauthorized client ID."), // scalastyle:ignore magic.number
    new ApiResponse(code = 404, message = "No data was found for the list of query Ids."), // scalastyle:ignore magic.number
    new ApiResponse(code = 500, message = "Internal server error."))) // scalastyle:ignore magic.number
  def timeSeries( // scalastyle:ignore public.methods.have.type
    @ApiParam(value = "The ID(s) of the data sources that you want observations from. Enter a comma-separated list to retrieve data from multiple sources. To retrieve a station, use the MET API station ID; e.g., _SN18700_ for Blindern. Retrieve the complete station lists using the <a href=\"https://data.met.no/docs#/sources\">sources</a> resource. Leave the query parameter empty to retrieve timeseries for all available stations.",
        required = false)
        sources: Option[String],
    @ApiParam(value = "The elements that you want observations for. Enter a comma-separated list to retrieve data for multiple elements. Elements follow the MET API naming convention and a complete list of all elements in the system can be retrieves using the <a href=\"https://data.met.no/docs#/elements\">elements</a> resource. leave the query parameter empty to retrieve timeseries for all available elements.",
        required = false)
        elements: Option[String],
    @ApiParam(value = "Fields to access",
        required = false)
        fields: Option[String],
    @ApiParam(value = "The output format of the result.",
        required = true,
        allowableValues = "jsonld",
        defaultValue = "jsonld")
        format: String) = no.met.security.AuthorizedAction {
    implicit request =>

    val start = DateTime.now(DateTimeZone.UTC)

    //var fieldList = Set.empty[Field]

    Try {
      val auth = request.headers.get("Authorization")
      val sourceList : Seq[String] = sources match {
        case Some(sources) => SourceSpecification.parse(sources)
        case _ => Seq()
      }
      val elementList : Seq[String] = elements match {
        case Some(elements) => elements split "," map (_ trim)
        case _ => Seq()
      }
      //fieldList = fieldSet(fields)
      dataAccess.getTimeSeries(elemTranslator, auth, sourceList, elementList)
    } match {
      case Success(data) =>
        if (data isEmpty) {
          NotFound("No data found")
        } else {
          format.toLowerCase() match {
            case "jsonld" => Ok(JsonTimeSeriesFormat.format(start, data)) as "application/vnd.no.met.data.observations.timeseries-v0+json"
            case x        => BadRequest(s"Invalid output format: $x")
          }
        }
      case Failure(x) => BadRequest(x getLocalizedMessage)
    }
  }

}

//$COVERAGE-ON$ 