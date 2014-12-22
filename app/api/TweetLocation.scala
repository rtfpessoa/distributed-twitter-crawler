package api

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.{JsPath, Reads}

case class TweetLocation(place: String, placeType: String, country: String)

object TweetLocation {
  implicit val locationReads: Reads[TweetLocation] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "place_type").read[String] and
      (JsPath \ "country").read[String]
    )(TweetLocation.apply _)
}
