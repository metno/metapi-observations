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
import no.met.data._
import no.met.time.TimeSpecification
import services.observations._

@Api(value = "/observations")
class ObservationsController @Inject()(dataAccess: DatabaseAccess, elemTranslator: ElementTranslator) extends Controller {

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
    @ApiParam(value = "The ID(s) of the data sources that you want observations from. Enter a comma-separated list to retrieve data from multiple sources. To retrieve a station, use the MET API station ID; e.g., _SN18700_ for Blindern. Retrieve the complete station lists using the <a href=docs#/sources>sources</a> resource.",
              required = true)
              sources: String,
    @ApiParam(value = "The time range that you want observations for. Time ranges are specified in an extended ISO-8601 format; see the reference section on <a href=references.html#time_specification>Time Specifications</a> for documentation and examples.",
              required = true)
              referencetime: String,
    @ApiParam(value = "The elements that you want observations for. Enter a comma-separated list to retrieve data for multiple elements. Elements follow the MET API naming convention and a complete list of all elements in the system can be retrieves using the <a href=docs#/elements>elements</a> resource.",
              required = true)
              elements: String,
    @ApiParam(value = "Return only data with the specified performance category. Enter a comma-separated list to specify multiple performance categories. Leave the query parameter empty to retrieve data regardless of its performance category.",
        required = false)
        performancecategory: Option[String],
    @ApiParam(value = "Return only data with the specified exposure category. Enter a comma-separated list to specify multiple exposure categories. Leave the query parameter empty to retrieve data regardless of exposure category.",
        required = false)
        exposurecategory: Option[String],
    @ApiParam(value = "Fields to access",
              required = false,
              allowableValues = "value,unit,qualityCode")
              fields: Option[String],
    @ApiParam(value = "The output format of the result.",
              required = true,
              allowableValues = "jsonld",
              defaultValue = "jsonld")
              format: String) = no.met.security.AuthorizedAction {
    implicit request =>
    val start = DateTime.now(DateTimeZone.UTC)
    val auth = request.headers.get("Authorization")
    val sourceDef = SourceSpecification.parse(Some(sources))
    val timeDef = TimeSpecification.parse(referencetime).get
    val elementDef = elements split "," map (_ trim)
    val perfList : Seq[String] = performancecategory match {
      case Some(performancecategory) => performancecategory split "," map (_ trim)
      case _ => Seq()
    }
    val expList : Seq[String] = exposurecategory match {
      case Some(exposurecategory) => exposurecategory split "," map (_ trim)
      case _ => Seq()
    }
    val fieldDef = FieldSpecification.parse(fields)
    Try {
      dataAccess.getObservations(auth, sourceDef, timeDef, elementDef, perfList, expList, fieldDef);
    } match {
      case Success(data) =>
        if (data isEmpty) {
          Error.error(NOT_FOUND, Some("No data found"), None, start)
        } else {
          format.toLowerCase() match {
            //case "csv"    => Ok(data.foldLeft(CsvFormat header data(0))(_ + '\n' + CsvFormat.format(_))) as "text/csv"
            case "jsonld" => Ok(JsonFormat.format(start, data)) as "application/vnd.no.met.data.observations-v0+json"
            case x        => Error.error(BAD_REQUEST, Some(s"Invalid output format: $x"), Some("Supported output formats: jsonld"), start)
          }
        }
      case Failure(x: BadRequestException) =>
        Error.error(BAD_REQUEST, Some(x getLocalizedMessage), x help, start)
      case Failure(x) =>
        Error.error(BAD_REQUEST, Some(x getLocalizedMessage), None, start)
    }
  }

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
    @ApiParam(value = "The time range that you want observations for. Time ranges are specified in an extended ISO-8601 format; see the reference section on <a href=references.html#time_specification>Time Specifications</a> for documentation and examples.",
        required = true)
        referencetime: Option[String],
    @ApiParam(value = "The elements that you want observations for. Enter a comma-separated list to retrieve data for multiple elements. Elements follow the MET API naming convention and a complete list of all elements in the system can be retrieves using the <a href=\"https://data.met.no/docs#/elements\">elements</a> resource. Leave the query parameter empty to retrieve timeseries for all available elements.",
        required = false)
        elements: Option[String],
    @ApiParam(value = "Return only time series with the specified performance category. Enter a comma-separated list to specify multiple performance categories. Leave the query parameter empty to retrieve timeseries for all available performance categories.",
        required = false)
        performancecategory: Option[String],
    @ApiParam(value = "Return only time series with the specified exposure category. Enter a comma-separated list to specify multiple exposure categories. Leave the query parameter empty to retrieve timeseries for all available exposure categories.",
        required = false)
        exposurecategory: Option[String],
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
    val auth = request.headers.get("Authorization")
    val sourceList = SourceSpecification.parse(sources)
    val timeDef = if (referencetime.isEmpty) None else Some(TimeSpecification.parse(referencetime.get).get)
    val elementList : Seq[String] = elements match {
      case Some(elements) => elements split "," map (_ trim)
      case _ => Seq()
    }
    val perfList : Seq[String] = performancecategory match {
      case Some(performancecategory) => performancecategory split "," map (_ trim)
      case _ => Seq()
    }
    val expList : Seq[String] = exposurecategory match {
      case Some(exposurecategory) => exposurecategory split "," map (_ trim)
      case _ => Seq()
    }
    val fieldDef = FieldSpecification.parse(fields)
    Try {
      dataAccess.getAvailableTimeSeries(auth, sourceList, timeDef, elementList, perfList, expList, fieldDef)
    } match {
      case Success(data) =>
        if (data isEmpty) {
          Error.error(NOT_FOUND, Some("No data found"), None, start)
        } else {
          format.toLowerCase() match {
            case "jsonld" => Ok(JsonTimeSeriesFormat.format(start, data)) as "application/vnd.no.met.data.observations.timeseries-v0+json"
            case x        => Error.error(BAD_REQUEST, Some(s"Invalid output format: $x"), Some("Supported output formats: jsonld"), start)
          }
        }
      case Failure(x: BadRequestException) =>
        Error.error(BAD_REQUEST, Some(x getLocalizedMessage), x help, start)
      case Failure(x) =>
        Error.error(BAD_REQUEST, Some(x getLocalizedMessage), None, start)
    }
  }

}
