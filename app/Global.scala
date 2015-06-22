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

//import scala.util.{ Success, Failure }
import com.google.inject.{ Guice, AbstractModule }
import play.api.{ Play, GlobalSettings }
import play.api.{ Application, Logger }
import no.met.kdvh._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started .......")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown .......")
  }
  /**
   *  Bind types such that whenever ElementTranslator is required, an instance of ElementTranslator1 will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      println("ok 1 ...")
      bind(classOf[ElementTranslator]).to(if (Play.isProd(Play.current)) classOf[ElementTranslator1] else classOf[ElementTranslator2])
    }
  })

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    println("ok 2 ...")
    injector.getInstance(controllerClass)
  }
}
