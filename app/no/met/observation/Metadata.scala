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

package no.met.observation

sealed trait GeomType {
  /**
   * Return the geometry as a WKT string. ex POINT( 9 62 )
   */
  def asWkt:String
}

case class Point( longitude: Double, latitude: Double, altitude: Option[Double] = None ) extends GeomType {
  def asWkt:String = s"POINT($longitude ${latitude}${altitude.map(" " + _).getOrElse("")})"
  override def toString():String = asWkt
}

/**
 * Helper class to decode geojson, geometry.
 */
case class Geometry( geom: GeomType )


trait Metadata {
  def elementUnit( element: String ): Option[String]
  def sourceCoordinate( source: String): Option[Geometry]
}
