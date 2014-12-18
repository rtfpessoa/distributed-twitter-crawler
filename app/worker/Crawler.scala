package worker

import models._
import play.api.Play.current
import play.api.libs.json.Reads.StringReads
import play.api.libs.json._
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WS
import play.api.{Logger, Play}
import rules.APILimitRules._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, MINUTES}
import scala.util.Try

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
    WorkTable.getById(workId).fold[Option[(Work, Boolean)]] {
      Logger.error(s"Could not get work $workId")
      None
    } {
      case work if work.workType == WorkType.Tweet =>
        val result = crawlTweets(work)
        Some((work, result.isDefined))

      case work if work.workType == WorkType.UserProfile =>
        val result = crawlUserProfile(work)
        Some((work, result.isDefined))
    }.map {
      case (work, true) =>
        WorkTable.update(work.copy(state = WorkState.Completed))
      case (work, false) =>
        WorkTable.update(work.copy(state = WorkState.Error))
    }
  }

  private def doRequest(endpoint: String): JsValue = {
    val consumerKey = ConsumerKey("cGy7r9WibBDFJPubjerLkLZ0J", "MSDYsH1psIXMRIrbm27sHc8PzXcsddVq1298duar59cxRM3ndt")
    val requestToken = RequestToken("2884611071-dbeInK4E3OtdZQPeIRdWgBDplXrU9al3IC3q8i9", "lwVsB6KWm3kowf9ikgtZ9h7pMVGmemV7mSoDhfqyaCCPP")

    val response = WS.url(s"https://api.twitter.com/1.1/$endpoint")
      .sign(OAuthCalculator(consumerKey, requestToken)).get()

    Await.result(response, Duration(1, MINUTES)).json
  }

  private def crawlTweets(work: Work) = {
    for {
      user <- UserTable.getById(work.userId)
      tweets <- getTweets(user.username, work.offset.getOrElse(100))
    } yield {
      val userTweets = tweets.map {
        tweet =>
          UserTweet(-1, user.id, tweet.text)
      }
      UserTweetTable.create(userTweets)

      tweets.map(_.mentions).flatten.map {
        apiUser =>
          val user = UserTable.create(User(-1, apiUser.username))
          UserDataTable.create(UserData(-1, user.id, apiUser.followersCount, apiUser.friendsCount))
      }
    }
  }

  private def getTweets(username: String, count: Int): Option[Seq[api.Tweet]] = withAPILimit("statuses/user_timeline") {
    doRequest(s"statuses/user_timeline.json?screen_name=$username&count=$count").asOpt[Seq[api.Tweet]]
  }

  private def crawlUserProfile(work: Work) = {
    UserTable.getById(work.userId).map {
      user =>
        val followers = getFollowers(user.username).getOrElse(Seq.empty)
        val friends = getFriends(user.username).getOrElse(Seq.empty)

        (followers ++ friends).flatMap {
          apiUser =>
            Try {
              val user = UserTable.create(User(-1, apiUser.username))
              UserDataTable.create(UserData(-1, user.id, apiUser.followersCount, apiUser.friendsCount))
            }.toOption
        }
    }
  }

  private def getFollowers(username: String): Option[Seq[api.User]] = withAPILimit("followers/list") {
    val json = doRequest(s"followers/list.json?cursor=-1&count=200&screen_name=$username&skip_status=true&include_user_entities=false")
    (json \ "users").asOpt[Seq[api.User]]
  }

  private def getFriends(username: String): Option[Seq[api.User]] = withAPILimit("friends/list") {
    val json = doRequest(s"friends/list.json?cursor=-1&count=200&screen_name=$username&skip_status=true&include_user_entities=false")
    (json \ "users").asOpt[Seq[api.User]]
  }

}
