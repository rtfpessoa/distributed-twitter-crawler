package worker

import models.{WorkTable, WorkType}
import play.api.Play.current
import play.api.libs.ws.WS
import play.api.{Logger, Play}

class Crawler {

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
        //crawlTweets(work)
      case Some(work) if work.workType == WorkType.UserProfile =>
        //crawlUserProfile(work)
      case _ =>
        Logger.error(s"Could not get work $workId")
    }
  }

}
