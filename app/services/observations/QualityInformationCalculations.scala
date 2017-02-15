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

import models.QualityInformation
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.TextualQualityInformation
import play.Logger
import scala.util._
import models.UserQualityInformation
import models.SingleQualityFlag
import models.QualityFlagInformation
import models.QualityFlagInformation
import scala.collection.SortedMap
import no.met.data.FieldSpecification
import no.met.data.BadRequestException
import scala.language.postfixOps

object QualityInformationCalculations {

  private val flagSize = 5

  def getAllInterpretations(language: String): Iterable[QualityFlagInformation] = {
    val parser = int("useinfo_id") ~ str("useinfo_name") ~ int("useinfo_flag") ~ str("description") map {
      case useinfo_id ~ name ~ flag ~ desc => (useinfo_id, name, flag, desc)
    }
    DB.withConnection("quality") { implicit c =>
      val v = SQL("""select useinfo_id, useinfo_name, useinfo_flag, description
            | from t_kdvh_useinfo_flag
            | where language={language}
            | and description not in ('Reserved', 'Reservert')
            | order by useinfo_id, useinfo_flag""".stripMargin).on(
        "language" -> databaseLanguageIdentifier(language)).as(parser *)

      SortedMap(v.groupBy(x => (x._1, x._2)).toSeq: _*).map { item =>
        QualityFlagInformation(item._1._2, item._2.map { flagValue =>
          SingleQualityFlag(flagValue._3, flagValue._4)
        })
      }
    }
  }

  def getDetailedQualityInformation(flag: String, language: String): scala.collection.immutable.IndexedSeq[TextualQualityInformation] = {
    val parser = int("useinfo_flag") ~ str("useinfo_name") ~ str("description") map {
      case flag ~ name ~ desc => TextualQualityInformation(name, flag, desc)
    }
    DB.withConnection("quality") { implicit c =>
      val tqio = (0 until flagSize) map { id =>
        SQL("""SELECT useinfo_flag, useinfo_name, description
                  | FROM t_kdvh_useinfo_flag
                  | WHERE useinfo_id={id} AND
                  | useinfo_flag={flag} AND
                  | language={language} AND
                  | description NOT IN ('Reserved', 'Reservert')""".stripMargin).on(
          "id" -> id,
          "flag" -> flag(id).asDigit,
          "language" -> databaseLanguageIdentifier(language)).as(
            parser.singleOpt)
      }
      tqio.flatten
    }
  }

  def getInterpretation(flag: String, fields: FieldSpecification, language: String): Option[QualityInformation] = {
    Try {
      if (flag.length != flagSize) {
        throw new BadRequestException(flag + ": No such flag") // invalid flag
      }
      val tqi = getDetailedQualityInformation(flag, language)

      if (tqi.size == flagSize) {
        val summarizedQuality = qualityCode(flag) match {
          case Some(f) => fields("summarized"){ () => qualityText(f, language) }
          case None => None
        }
        Some(QualityInformation(summarizedQuality, fields("flag"){()=>flag}, fields("details"){()=>tqi}))
      } else {
        None
      }
    } match {
      case scala.util.Success(qi) => qi
      case Failure(x) => throw new BadRequestException(flag + ": No such flag")
    }
  }

  // scalastyle:off magic.number
  // scalastyle:off cyclomatic.complexity
  def qualityCode(qualityFlag: String): Option[Int] = {
    if (qualityFlag.length == flagSize) {
      (qualityFlag.charAt(2), qualityFlag.charAt(3)) match {
        case (c: Char, '1') => Some(1) // OK
        case (c: Char, '2') => Some(1) // OK
        case (c: Char, '3') => Some(6) // Uncertain
        case (c: Char, '4') => Some(6) // Uncertain
        case (c: Char, '5') => Some(1) // OK
        case (c: Char, '6') => Some(1) // OK
        case ('0', c: Char) => Some(0) // OK
        case ('9', c: Char) => Some(2) // Uncertain
        case ('1', '0') => Some(5) // Uncertain
        case ('2', '0') => Some(5) // Very uncertain
        case ('3', '0') => Some(7) // Erroneous
        case ('3', '8') => Some(7) // Erroneous
        case _ => None // Undefined
      }
    } else {
      None
    }
  }

  def qualityText(flag: Int, language: String): UserQualityInformation = {
    val parser = int("flag_level") ~ str("flag_text") ~ str("flag_description") map {
      case value ~ shortMeaninig ~ meaning => UserQualityInformation(value, shortMeaninig, meaning)
    }
    Try {
      DB.withConnection("quality") { implicit c =>
        SQL("""select flag_level, flag_text, flag_description
                | from t_kdvh_user_flag
                | where flag_level={flag} and
                | language={language};""".stripMargin).on(
          "flag" -> flag,
          "language" -> databaseLanguageIdentifier(language)).as(
            parser.single)
      }
    } match {
      case scala.util.Success(qi) => qi
      case Failure(x) => throw new Exception(flag + ": No such flag")
    }
  }

  /**
   * Translate language identifiers, since database uses other names.
   */
  private def databaseLanguageIdentifier(language: String): String = language match {
    case "nb-NO" => "no"
    case "nn-NO" => "es"
    case "en-US" => "en"
    case _ => {
      Logger.warn("Encountered invalid language identifier. Assuming en-US")
      "en"
    }
  }
}
