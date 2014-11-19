package worker

import java.util.concurrent.LinkedBlockingQueue

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import models.{Work, WorkTable, WorkType}
import play.api.Play.current
import play.api.libs.ws.WS
import play.api.{Logger, Play}

object Crawler {

  def register(): Boolean = {
    Play.configuration.getString("dtc.mastermind.ip").exists {
      masterIp =>
        val myUrl = s"http://${Play.configuration.getString("dtc.worker.ip").getOrElse("localhost")}:${Play.configuration.getInt("dtc.worker.port").getOrElse(9000)}"
        val url = s"http://$masterIp${controllers.routes.MastermindController.addWorker(myUrl)}"
        println(url)
        WS.url(url).get()
        true
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

  private def crawlTweets(work: Work) = test()

  private def crawlUserProfile(work: Work) = test()

  private def test(): Unit = {
    val stringQueue = new LinkedBlockingQueue[String](10000)
    val eventQueue = new LinkedBlockingQueue[Event](10000)

    //    val auth = new OAuth1(consumerKey, consumerSecret, token, secret)
    //      .authentication(auth)

    val builder = new ClientBuilder()
      .hosts(Constants.STREAM_HOST)
      .endpoint(new StatusesSampleEndpoint())
      .processor(new StringDelimitedProcessor(stringQueue))
      .eventMessageQueue(eventQueue)

    val hosebirdClient = builder.build()

    hosebirdClient.connect()
    while (!hosebirdClient.isDone) {
      val message = stringQueue.take()
      println(message)
    }
  }

}
