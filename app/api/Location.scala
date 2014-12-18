package api

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.{JsPath, Reads}

case class Location(place: String, placeType: String, country: String)

object Location {
  implicit val locationReads: Reads[Location] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "place_type").read[String] and
      (JsPath \ "country").read[String]
    )(Location.apply _)
}
