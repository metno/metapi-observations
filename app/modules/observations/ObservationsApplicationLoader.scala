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
package modules.observations

import play.api._
import play.api.inject.guice._
import play.api.inject.guice.GuiceableModule.fromGuiceModule
import com.google.inject.AbstractModule
import services.observations._

/**
 * Configurations for Production Mode
 */
// $COVERAGE-OFF$ Can't test the production binding in Test mode
class ObservationsProdModule extends AbstractModule {

  def configure() {
    bind(classOf[DatabaseAccess]).to(classOf[KdvhDatabaseAccess])
    bind(classOf[ElementInfoGetter]).to(classOf[ProdElementInfoGetter])
  }

}
// $COVERAGE-ON$

/**
 * Configurations for Development Mode
 */
class ObservationsNonProdModule extends AbstractModule {

  def configure() {
    bind(classOf[DatabaseAccess]).to(classOf[MockDatabaseAccess])
    bind(classOf[ElementInfoGetter]).to(classOf[MockElementInfoGetter])
  }

}

/**
 * Set up the Guice injector and provide the mechanism to return objects from the dependency graph.
 */
// $COVERAGE-OFF$ Can't test the production binding in Test mode
class ObservationsApplicationLoader extends GuiceApplicationLoader() {

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val builder = initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration)
      .overrides(overrides(context): _*)

    Mode.Prod match {
      case Mode.Prod =>
        builder.bindings(new ObservationsProdModule)
      case _ =>
        builder.bindings(new ObservationsNonProdModule)
    }
  }

}
// $COVERAGE-ON$
