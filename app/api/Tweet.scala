package api

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.{JsPath, Reads}

case class Tweet(twitterId: Long, created_at: DateTime, text: String, urls: Seq[String],
                 mentions: Seq[String], hashtags: Seq[String], location: Option[TweetLocation])

object Tweet {
  val dateFormat = "E MMM d HH:mm:ss Z y"
  implicit val jodaDateTimeReads = Reads.jodaDateReads(dateFormat)

  val urlReads = Reads.seq((JsPath \ "expanded_url").read[String])
  val mentionReads = Reads.seq((JsPath \ "screen_name").read[String])
  val hashtagReads = Reads.seq((JsPath \ "text").read[String])

  implicit val tweetReads: Reads[Tweet] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "created_at").read[DateTime] and
      (JsPath \ "text").read[String] and
      (JsPath \ "entities" \ "urls").read(urlReads) and
      (JsPath \ "entities" \ "user_mentions").read(mentionReads) and
      (JsPath \ "entities" \ "hashtags").read(hashtagReads) and
      (JsPath \ "place").readNullable[TweetLocation]
    )(Tweet.apply _)
}
