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

package services.observations

import com.github.nscala_time.time.Imports._

case class ApiCommon(val context: String = "https://data.met.no/schema/",
  val apiVersion: String = "0.1.0",
  val license: String = "http://met.no/English/Data_Policy_and_Data_Services/")

case class CommonResult(
  val created: DateTime,
  val duration: Duration,
  startOffset: Long,
  currentItemCount: Long,
  itemsPerPage: Long,
  totalItemCount: Long,
  nextLink: Option[String],
  prevLink: Option[String],
  apiCommon: ApiCommon = ApiCommon())
