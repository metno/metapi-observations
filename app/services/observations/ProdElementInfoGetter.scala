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

import play.api.Play.current
import play.api.http.Status._
import play.api.libs.ws._
import play.api.libs.json._
import play.Logger
import javax.inject.Singleton
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util._
import ExecutionContext.Implicits.global
import no.met.data._

//$COVERAGE-OFF$ Unit tests cannot cover database access code

/**
  * Concrete implementation of ElementInfoGetter.
  */
@Singleton
class ProdElementInfoGetter extends ElementInfoGetter {

  /**
    * Get element info from the elements/ endpoint in the [staging-]data.met.no web service.
    */
  override def getInfoMap(auth: Option[String], requestHost: String, elementIds: Set[String]): Map[String, ElementInfo] = {

    val host = if (requestHost.matches("""data\.met\.no.*""")) "data.met.no" else "staging-data.met.no"
    val finalAuth = auth.getOrElse(s"Basic ${host match {
      case "data.met.no" => "NjU5OTVjZDYtMTE0NS00MDdmLTllNjUtOWMzMTU5ODg5NDk2"  // anonymous ID for data.met.no
      case _ => "ZWNlMTA4NzQtYzQ3My00MmY0LTgxZTUtNmM1NjhkNDgzYWFj" // anonymous ID for staging-data.met.no
    }}")

    val request: WSRequest = WS.url(s"https://$host/elements/v0.jsonld")
      .withHeaders("Authorization" -> finalAuth)
      .withQueryString("ids" -> elementIds.mkString(","))
    val response: Future[WSResponse] = request.get()
    val result = Await.result(response, 10 seconds)
    result.status match {
      case OK => {
        (result.json \ "data").asOpt[JsArray] match {
          case Some(data) => {
            data.value.map((v: JsValue) => (((v \ "id").asOpt[String] match {
              case Some(id) => id
              case None => throw new InternalServerErrorException("Item in response from elements/ endpoint is missing 'id' field")
            }) -> ElementInfo(v))).toMap
          }
          case None => throw new InternalServerErrorException("No 'data' array found in response from elements/ endpoint")
        }
      }
      case _  => throw new InternalServerErrorException(s"Failed to get element info for ${elementIds.size} element(s) (status code: ${result.status})")
    }
  }

}

// $COVERAGE-ON$
