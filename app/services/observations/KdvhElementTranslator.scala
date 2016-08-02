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

import javax.inject.Singleton
import play.api.Play.current
import play.api.libs.ws._
import play.api.http.Status._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import ExecutionContext.Implicits.global

import play.api.Logger

//$COVERAGE-OFF$Not testing database queries

/**
 * Concrete implementation of KdvhElementTranslator class.
 * Note that this connects to the data.met.no WS, not the KDVH database
 */
@Singleton
class KdvhElementTranslator extends ElementTranslator {

  override def toKdvhElemName(auth: Option[String], apiElemName: String): String = {
    // if auth is null, it is because authorization is off
    val authStr = auth.getOrElse("Basic invalid-id")
    val baseUrl = current.configuration.getString("met.elements.baseUrl") getOrElse "https://data.met.no/elements/v0.jsonld"
    val request: WSRequest = WS.url(baseUrl)
                               .withHeaders("Authorization" -> authStr)
                               .withQueryString("id" -> apiElemName)
    val response: Future[WSResponse] = request.get()

    val result = Await.result(response, 2 seconds)
    result.status match {
      case OK => ((result.json \ "data")(0) \ "kdvhConvention" \ "code").get.as[String]
      case _  => throw new Exception("Failed to translate to KDVH element name: " + apiElemName)
    }

  }

  override def toApiElemName(auth: Option[String], kdvhElemName: String): String = {
    // if auth is null, it is because authorization is off
    val authStr = auth.getOrElse("Basic invalid-id")
    val baseUrl = current.configuration.getString("met.elements.baseUrl") getOrElse "https://data.met.no/elements/v0.jsonld"
    val request: WSRequest = WS.url(baseUrl)
                               .withHeaders("Authorization" -> authStr)
                               .withQueryString("code" -> kdvhElemName)
    val response: Future[WSResponse] = request.get()

    val result = Await.result(response, 2 seconds)
    result.status match {
      case OK => ((result.json \ "data")(0) \ "id").get.as[String]
      case _  => throw new Exception("Failed to translate to API element name: " + kdvhElemName)
    }

  }

}

// $COVERAGE-ON$
