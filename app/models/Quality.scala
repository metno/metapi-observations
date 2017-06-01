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

package models

import io.swagger.annotations._
import scala.annotation.meta.field
import no.met.data._
import java.net.URL
import org.joda.time._

@ApiModel(description = "Quality information for an observation")
case class QualityInformation(
  @(ApiModelProperty @field)(value = "Summarized quality.") summarized: Option[UserQualityInformation],
  @(ApiModelProperty @field)(value = "The quality code flag.", example = "70000") flag: Option[String],
  @(ApiModelProperty @field)(value = "details") details: Option[Seq[TextualQualityInformation]])

@ApiModel(description = "Textual description of a quality flag")
case class TextualQualityInformation(
  @(ApiModelProperty @field)(value = "controlType", example = "Control level passed") controlType: String,
  @(ApiModelProperty @field)(value = "value", example = "3") value: Int,
  @(ApiModelProperty @field)(value = "meaninig", example = "Time of observation deviates from the norm") meaning: String)

case class SingleQualityFlag(
  @(ApiModelProperty @field)(value = "value", example = "3") value: Int,
  @(ApiModelProperty @field)(value = "meaninig", example = "Time of observation deviates from the norm") meaning: String)

case class DetailedQualityFlagInformation(
    @(ApiModelProperty @field)(value = "controlType", example = "Quality of original value") controlType: String,
    @(ApiModelProperty @field)(value = "values") values: Seq[SingleQualityFlag])

@ApiModel(description = "Textual description of all possible quality flags")
case class FullQualityFlagInformation(
    @(ApiModelProperty @field)(value = "summarized") summarized: Option[Iterable[UserQualityInformation]],
    @(ApiModelProperty @field)(value = "details") details: Option[Iterable[DetailedQualityFlagInformation]])

@ApiModel(description = "Data response for observation time series.")
case class QualityFlagInformationResponse(
  @(ApiModelProperty @field)(name = ApiConstants.CONTEXT_NAME, value = ApiConstants.CONTEXT, example = ApiConstants.METAPI_CONTEXT) context: URL,
  @(ApiModelProperty @field)(name = ApiConstants.OBJECT_TYPE_NAME, value = ApiConstants.OBJECT_TYPE, example = "ObservationTimeSeriesResponse") responseType: String,
  @(ApiModelProperty @field)(value = ApiConstants.API_VERSION, example = ApiConstants.API_VERSION_EXAMPLE) apiVersion: String,
  @(ApiModelProperty @field)(value = ApiConstants.LICENSE, example = ApiConstants.METAPI_LICENSE) license: URL,
  @(ApiModelProperty @field)(value = ApiConstants.CREATED_AT, dataType = "String", example = ApiConstants.CREATED_AT_EXAMPLE) createdAt: DateTime,
  @(ApiModelProperty @field)(value = ApiConstants.QUERY_TIME, dataType = "String", example = ApiConstants.QUERY_TIME_EXAMPLE) queryTime: Duration,
  @(ApiModelProperty @field)(value = ApiConstants.CURRENT_ITEM_COUNT, example = ApiConstants.CURRENT_ITEM_COUNT_EXAMPLE) currentItemCount: Long,
  @(ApiModelProperty @field)(value = ApiConstants.ITEMS_PER_PAGE, example = ApiConstants.ITEMS_PER_PAGE_EXAMPLE) itemsPerPage: Long,
  @(ApiModelProperty @field)(value = ApiConstants.OFFSET, example = ApiConstants.OFFSET_EXAMPLE) offset: Long,
  @(ApiModelProperty @field)(value = ApiConstants.TOTAL_ITEM_COUNT, example = ApiConstants.TOTAL_ITEM_COUNT_EXAMPLE) totalItemCount: Long,
  @(ApiModelProperty @field)(value = ApiConstants.NEXT_LINK, example = ApiConstants.NEXT_LINK_EXAMPLE) nextLink: Option[URL],
  @(ApiModelProperty @field)(value = ApiConstants.PREVIOUS_LINK, example = ApiConstants.PREVIOUS_LINK_EXAMPLE) previousLink: Option[URL],
  @(ApiModelProperty @field)(value = ApiConstants.CURRENT_LINK, example = ApiConstants.CURRENT_LINK_EXAMPLE) currentLink: URL,
  @(ApiModelProperty @field)(value = ApiConstants.DATA) data: FullQualityFlagInformation)
    extends BasicResponse(context, responseType, apiVersion, license, createdAt, queryTime, currentItemCount, itemsPerPage, offset, totalItemCount,
      nextLink, previousLink, currentLink)


@ApiModel(description = "Textual description of a user quality flag")
case class UserQualityInformation(
  @(ApiModelProperty @field)(value = "value", example = "3") value: Int,
  @(ApiModelProperty @field)(value = "shortMeaning", example = "OK") shortMeaning: String,
  @(ApiModelProperty @field)(value = "meaninig", example = " Value is slightly uncertain") meaning: String)

@ApiModel(description = "Data response for observation time series.")
case class QualityInformationResponse(
  @(ApiModelProperty @field)(name = ApiConstants.CONTEXT_NAME, value = ApiConstants.CONTEXT, example = ApiConstants.METAPI_CONTEXT) context: URL,
  @(ApiModelProperty @field)(name = ApiConstants.OBJECT_TYPE_NAME, value = ApiConstants.OBJECT_TYPE, example = "ObservationTimeSeriesResponse") responseType: String,
  @(ApiModelProperty @field)(value = ApiConstants.API_VERSION, example = ApiConstants.API_VERSION_EXAMPLE) apiVersion: String,
  @(ApiModelProperty @field)(value = ApiConstants.LICENSE, example = ApiConstants.METAPI_LICENSE) license: URL,
  @(ApiModelProperty @field)(value = ApiConstants.CREATED_AT, dataType = "String", example = ApiConstants.CREATED_AT_EXAMPLE) createdAt: DateTime,
  @(ApiModelProperty @field)(value = ApiConstants.QUERY_TIME, dataType = "String", example = ApiConstants.QUERY_TIME_EXAMPLE) queryTime: Duration,
  @(ApiModelProperty @field)(value = ApiConstants.CURRENT_ITEM_COUNT, example = ApiConstants.CURRENT_ITEM_COUNT_EXAMPLE) currentItemCount: Long,
  @(ApiModelProperty @field)(value = ApiConstants.ITEMS_PER_PAGE, example = ApiConstants.ITEMS_PER_PAGE_EXAMPLE) itemsPerPage: Long,
  @(ApiModelProperty @field)(value = ApiConstants.OFFSET, example = ApiConstants.OFFSET_EXAMPLE) offset: Long,
  @(ApiModelProperty @field)(value = ApiConstants.TOTAL_ITEM_COUNT, example = ApiConstants.TOTAL_ITEM_COUNT_EXAMPLE) totalItemCount: Long,
  @(ApiModelProperty @field)(value = ApiConstants.NEXT_LINK, example = ApiConstants.NEXT_LINK_EXAMPLE) nextLink: Option[URL],
  @(ApiModelProperty @field)(value = ApiConstants.PREVIOUS_LINK, example = ApiConstants.PREVIOUS_LINK_EXAMPLE) previousLink: Option[URL],
  @(ApiModelProperty @field)(value = ApiConstants.CURRENT_LINK, example = ApiConstants.CURRENT_LINK_EXAMPLE) currentLink: URL,
  @(ApiModelProperty @field)(value = ApiConstants.DATA) data: QualityInformation)
    extends BasicResponse(context, responseType, apiVersion, license, createdAt, queryTime, currentItemCount, itemsPerPage, offset, totalItemCount,
      nextLink, previousLink, currentLink)
