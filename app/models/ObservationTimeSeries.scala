/*
    MET-API

    Copyright (C) 2016 met.no
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
import no.met.data.{ ApiConstants, BasicResponse }
import no.met.geometry.{ Level, Point }

@ApiModel(description = "Data response for observation time series.")
case class ObservationTimeSeriesResponse(
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
  @(ApiModelProperty @field)(value = ApiConstants.DATA) data: Seq[ObservationTimeSeries])
    extends BasicResponse(context, responseType, apiVersion, license, createdAt, queryTime, currentItemCount, itemsPerPage, offset, totalItemCount,
      nextLink, previousLink, currentLink)

@ApiModel(description = "Recorded time series that exist for these parameters.")
case class ObservationTimeSeries(
  @(ApiModelProperty @field)(value = "The sourceId at which this series of values were observed.", example = "SN18700") sourceId: Option[String],
  @(ApiModelProperty @field)(value = "Spatial location of the data when it was observed (if known).") geometry: Option[Point],
  @(ApiModelProperty @field)(value = "The level of the data when it was observed (if known).", example = "74") levels: Option[Seq[Level]],
  @(ApiModelProperty @field)(value = "The datetime from which data exists in this timeseries.", example = "1974-05-29") validFrom: Option[String],
  @(ApiModelProperty @field)(value = "The datetime to which data exists in this timeseries. Null if data is still being added to the time series.", example = "1977-05-16") validTo: Option[String],
  @(ApiModelProperty @field)(value = "The offset from the validFrom datetime. Add the offset to validFrom to get the referenceTime of the first observation in the timeseries.", example = "P18H") timeOffset: Option[String],
  @(ApiModelProperty @field)(value = "The interval between results in the timeseries. Given the reference time of an observation, add the resultTimeInterval to get the next observation in the timeseries.", example = "P24H") resultTimeInterval: Option[String],
  @(ApiModelProperty @field)(value = "The MET API id of the element observed.", example = "air_temperature") elementId: Option[String],
  @(ApiModelProperty @field)(value = "The unit of measure of the observed data. *code* if the unit is described using a code table.", example = "degC") unit: Option[String],
  @(ApiModelProperty @field)(value = "If the unit is a *code*, the codetable that describes the codes used.", example = "beaufort_scale") codeTable: Option[String],
  @(ApiModelProperty @field)(value = "The performance category.") performanceCategory: Option[String],
  @(ApiModelProperty @field)(value = "The performance category.") exposureCategory: Option[String],
  @(ApiModelProperty @field)(value = "The status of the timeseries.") status: Option[String],
  @(ApiModelProperty @field)(value = "The URI that can be used to retrieve this timeseries.") uri: Option[String])
