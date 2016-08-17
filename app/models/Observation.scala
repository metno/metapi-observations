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

import com.github.nscala_time.time.Imports._
import io.swagger.annotations._
import java.net.URL
import scala.annotation.meta.field
import scala.collection._
import no.met.data.{ApiConstants,BasicResponse}

@ApiModel(description="Data response for observation data.")
case class ObservationResponse(
  @(ApiModelProperty @field)(name=ApiConstants.CONTEXT_NAME, value=ApiConstants.CONTEXT, example=ApiConstants.METAPI_CONTEXT) context: URL,
  @(ApiModelProperty @field)(name=ApiConstants.OBJECT_TYPE_NAME, value=ApiConstants.OBJECT_TYPE, example="ObservationResponse") responseType: String,
  @(ApiModelProperty @field)(value=ApiConstants.API_VERSION, example=ApiConstants.API_VERSION_EXAMPLE) apiVersion: String,
  @(ApiModelProperty @field)(value=ApiConstants.LICENSE, example=ApiConstants.METAPI_LICENSE) license: URL,
  @(ApiModelProperty @field)(value=ApiConstants.CREATED_AT, dataType="String", example=ApiConstants.CREATED_AT_EXAMPLE) createdAt: DateTime,
  @(ApiModelProperty @field)(value=ApiConstants.QUERY_TIME, dataType="String", example=ApiConstants.QUERY_TIME_EXAMPLE) queryTime: Duration,
  @(ApiModelProperty @field)(value=ApiConstants.CURRENT_ITEM_COUNT, example=ApiConstants.CURRENT_ITEM_COUNT_EXAMPLE) currentItemCount: Long,
  @(ApiModelProperty @field)(value=ApiConstants.ITEMS_PER_PAGE, example=ApiConstants.ITEMS_PER_PAGE_EXAMPLE) itemsPerPage: Long,
  @(ApiModelProperty @field)(value=ApiConstants.OFFSET, example=ApiConstants.OFFSET_EXAMPLE) offset: Long,
  @(ApiModelProperty @field)(value=ApiConstants.TOTAL_ITEM_COUNT, example=ApiConstants.TOTAL_ITEM_COUNT_EXAMPLE) totalItemCount: Long,
  @(ApiModelProperty @field)(value=ApiConstants.NEXT_LINK, example=ApiConstants.NEXT_LINK_EXAMPLE) nextLink: Option[URL],
  @(ApiModelProperty @field)(value=ApiConstants.PREVIOUS_LINK, example=ApiConstants.PREVIOUS_LINK_EXAMPLE) previousLink: Option[URL],
  @(ApiModelProperty @field)(value=ApiConstants.CURRENT_LINK, example=ApiConstants.CURRENT_LINK_EXAMPLE) currentLink: URL,
  @(ApiModelProperty @field)(value=ApiConstants.DATA) data: Seq[ObservationSeries]
) 
extends BasicResponse( context, responseType, apiVersion, license, createdAt, queryTime, currentItemCount, itemsPerPage, offset, totalItemCount, nextLink, previousLink, currentLink)

@ApiModel(description="Observations at the defined source.")
case class ObservationSeries(
  @(ApiModelProperty @field)(value="The sourceId at which this series of values were observed.", example="SN18700") sourceId: String,
  @(ApiModelProperty @field)(value="The values observed at this source.") observations: Seq[Observation]
)

@ApiModel(description="Observations at the specified time.")
case class Observation(
  @(ApiModelProperty @field)(value="The time at which the observation was generated/observed.", dataType="String", example="2012-12-24T11:00:00Z") referenceTime: DateTime,
  @(ApiModelProperty @field)(value="The values observed at this time.") values: Seq[ObservedElement]
)

@ApiModel(description="Observed elements for the specified source and reference time.")
case class ObservedElement(
  @(ApiModelProperty @field)(value="The MET API id of the element observed.", example="air_temperature") elementId: Option[String],
  @(ApiModelProperty @field)(value="The value of the observed data.", example="12.7") value: Option[Double],
  @(ApiModelProperty @field)(value="The unit of measure of the observed data.", example="degC") unit: Option[String],
  @(ApiModelProperty @field)(value="The quality code of the observed data value.", example="700000") qualityCode: Option[String]
)
