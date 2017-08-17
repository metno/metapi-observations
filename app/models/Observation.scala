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

import anorm.{Row, Column, MetaDataItem}


import com.github.nscala_time.time.Imports._
import io.swagger.annotations._
import java.net.URL
import scala.annotation.meta.field
import scala.collection._
import scala.util._
import no.met.data.{ApiConstants, BasicResponse, ObsValue}
import no.met.geometry._
import java.sql.Timestamp
import services.observations.QualityInformationCalculations

// scalastyle:off line.size.limit

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
extends BasicResponse( context, responseType, apiVersion, license, createdAt, queryTime, currentItemCount, itemsPerPage, offset, totalItemCount,
    nextLink, previousLink, currentLink)

@ApiModel(description="Observations at the defined source.")
case class ObservationSeries(
  @(ApiModelProperty @field)(value="The sourceId at which this series of values were observed.", example="SN18700") sourceId: Option[String],
  @(ApiModelProperty @field)(value="Spatial location of the data when it was observed (if known).") geometry: Option[Point],
  @(ApiModelProperty @field)(value="The level of the data when it was observed (if known).") levels: Option[Level],
  @(ApiModelProperty @field)(value="The time at which the observation was generated/observed.", dataType="String", example="2012-12-24T11:00:00Z") referenceTime: Option[String],
  @(ApiModelProperty @field)(value="The values observed at this source. This is a map of the form [ElementId (as a String), Observation]") observations: Option[Seq[Observation]]
)

@ApiModel(description="Observations at the specified time.")
case class Observation(
  @(ApiModelProperty @field)(value="The id of the element being observed.", example="air_temperature") elementId: Option[String],
  @(ApiModelProperty @field)(value="The value of the observation.", example="12.7") value: Option[ObsValue],
  @(ApiModelProperty @field)(value="The unit of measure of the observed data.", example="degC") unit: Option[String],
  @(ApiModelProperty @field)(value="If the unit is a *code*, the codetable that describes the codes used.", example="beaufort_scale") codeTable: Option[String],
  @(ApiModelProperty @field)(value="The performance category of the source when the value was observed.", example="A") performanceCategory: Option[String],
  @(ApiModelProperty @field)(value="The exposure category of the source when the value was observed.", example="1") exposureCategory: Option[String],
  @(ApiModelProperty @field)(value="The quality control flag of the observed data value.", example="0") qualityCode: Option[Int],
  @(ApiModelProperty @field)(value="The data version of the data value, if one exists (**Note: Currently not available for any observation data).", example="3") dataVersion: Option[Int]
)

// Observation Meta information retrieved from KDVH
case class ObservationMeta(
    kdvhStNr: Int,
    kdvhSensorNr: Int,
    sensorLevel: Option[Level],
    kdvhElemCode: String,
    elementId: String,
    elementUnit: Option[String],
    elementCode: Option[String],
    valueTable: String,
    flagTable: Option[String],
    performanceCategory: String,
    exposureCategory: String
)

case class Level(
  @(ApiModelProperty @field)(value="The level type defining the reference for the level value.", example="height_above_ground") levelType: Option[String],
  @(ApiModelProperty @field)(value="The unit of measure of the level data.", example="m") unit: Option[String],
  @(ApiModelProperty @field)(value="The level values.", example="[5, 10, 20]") value: Option[Int]
)

// Observation Values from KDVH query
case class ObservedData(value: Option[ObsValue], quality: Option[String] = None)

// $COVERAGE-OFF$ Mapping of the database query to a Seq of Observation Series
object ObservationSeries {

  implicit def columnToObsValue: Column[ObsValue] =
    Column.nonNull1 { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      ObsValue.toObsValue(value, qualified)
    }


  def apply(row: Row, obsMeta:List[ObservationMeta], kdvhElements: Set[String]): List[ObservationSeries] = {
    kdvhElements.foldLeft(List.empty[ObservationSeries]) {
      (l, elem) =>
        val stationId = row[Int]("stationid")
        val refTime = row[String]("referencetime")
        val meta = obsMeta.find( m => m.kdvhStNr == stationId && m.kdvhElemCode == elem)
        val sensor = if (meta.isEmpty) 0 else meta.get.kdvhSensorNr
        val value = row[Option[ObsValue]](elem)
        val quality = Try { row[Option[String]](s"${elem}_flag") } match {
          case Success(flag) => flag
          case Failure(x) => None
        }
        if (!value.isEmpty) {
          l :+ new ObservationSeries(
                Some("SN" + stationId + ":" + sensor ),
                None,
                meta.get.sensorLevel,
                Some(refTime),
                Some(Seq(Observation(Some(meta.get.elementId),
                                     value,
                                     meta.get.elementUnit,
                                     meta.get.elementCode,
                                     Some(meta.get.performanceCategory),
                                     Some(meta.get.exposureCategory),
                                     quality match {
                                      case Some(q) => QualityInformationCalculations.qualityCode(q)
                                      case None => None
                                     },
                                     None))))
        } else {
          l
        }
    }
  }

}
//$COVERAGE-ON$

// scalastyle:on
