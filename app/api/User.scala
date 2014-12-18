package api

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.{JsPath, Reads}

case class User(username: String, followersCount: Long, friendsCount: Long)

object User {
  implicit val userReads: Reads[User] = (
    (JsPath \ "screen_name").read[String] and
      (JsPath \ "followers_count").read[Long] and
      (JsPath \ "friends_count").read[Long]
    )(User.apply _)
}
