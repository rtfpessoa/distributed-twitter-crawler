package worker

import models.{UserTable, Work, WorkTable, WorkType}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.functional.syntax._
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

    val a = Await.result(response, Duration(1, MINUTES)).json.asOpt[List[Tweet]]
    println(a)
  }

}


case class Tweet(tweetId: Long/*, created_at: DateTime*/, text: String /*url: Seq[String], mentions: Seq[String] /*, coordinates: Option[String]*/*/)

object Tweet {
  //"Wed Aug 29 17:12:58 +0000 2012"

  val dateFormat = "E M d HH:mm:ss Z y"
  implicit val jodaDateTimeReads = Reads.jodaDateReads(dateFormat)

  implicit val locationReads: Reads[Tweet] = (
    (JsPath \ "id").read[Long] and
      //(JsPath \ "created_at").read[DateTime] and
      (JsPath \ "text").read[String] /*and
      (JsPath \ "entities" \ "urls" \\ "url").read[Seq[String]] and
      (JsPath \ "entities" \ "user_mentions" \\ "screen_name").read[Seq[String]] //and
    // (JsPath \ "coordinates").read[Option[String]]*/
    )(Tweet.apply _)
}