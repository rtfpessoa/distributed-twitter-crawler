package worker

import models.{UserTable, Work, WorkTable, WorkType}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json._
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WS
import play.api.{Logger, Play}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, MINUTES}

object Crawler {

  def register(): Unit = {
    Play.configuration.getString("dtc.mastermind.ip").map {
      masterIp =>
        val myUrl = s"http://${Play.configuration.getString("http.address").getOrElse("127.0.0.1")}:${Play.configuration.getInt("http.port").getOrElse(9000)}"
        val url = s"http://$masterIp${controllers.routes.MastermindController.addWorker(myUrl)}"
        WS.url(url).get().map {
          response =>
            (response.json \ "success").asOpt[String].getOrElse {
              Logger.error(s"Could not register worker: $myUrl")
              sys.exit(1)
            }
        }
    }
  }

  def newWork(workId: Long) = {
    WorkTable.getById(workId) match {
      case Some(work) if work.workType == WorkType.Tweet =>
        crawlTweets(work)
      case Some(work) if work.workType == WorkType.UserProfile =>
        crawlUserProfile(work)
      case _ =>
        Logger.error(s"Could not get work $workId")
    }
  }

  private def crawlTweets(work: Work) = {
    UserTable.getById(work.userId).map {
      user =>
        getTweets(user.username, work.offset.getOrElse(100))
    }
  }

  private def crawlUserProfile(work: Work) = {}

  private def getTweets(username: String, count: Int) = {
    val consumerKey = ConsumerKey("cGy7r9WibBDFJPubjerLkLZ0J", "MSDYsH1psIXMRIrbm27sHc8PzXcsddVq1298duar59cxRM3ndt")
    val requestToken = RequestToken("2884611071-dbeInK4E3OtdZQPeIRdWgBDplXrU9al3IC3q8i9", "lwVsB6KWm3kowf9ikgtZ9h7pMVGmemV7mSoDhfqyaCCPP")

    val response = WS.url(s"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=$username&count=$count")
      .sign(OAuthCalculator(consumerKey, requestToken)).get()

    Await.result(response, Duration(1, MINUTES)).json.asOpt[List[Tweet]]
  }

}

case class Location(place: String, placeType: String, country: String)

case class Tweet(tweetId: Long, created_at: DateTime, text: String, urls: Seq[String],
                 mentions: Seq[String], hashtags: Seq[String], location: Option[Location])

object Tweet {
  val dateFormat = "E MMM d HH:mm:ss Z y"
  implicit val jodaDateTimeReads = Reads.jodaDateReads(dateFormat)

  val urlReads = Reads.seq((JsPath \ "expanded_url").read[String])
  val mentionReads = Reads.seq((JsPath \ "screen_name").read[String])
  val hashtagReads = Reads.seq((JsPath \ "text").read[String])

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "place_type").read[String] and
      (JsPath \ "country").read[String]
    )(Location.apply _)

  implicit val tweetReads: Reads[Tweet] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "created_at").read[DateTime] and
      (JsPath \ "text").read[String] and
      (JsPath \ "entities" \ "urls").read(urlReads) and
      (JsPath \ "entities" \ "user_mentions").read(mentionReads) and
      (JsPath \ "entities" \ "hashtags").read(hashtagReads) and
      (JsPath \ "place").readNullable[Location]
    )(Tweet.apply _)
}
