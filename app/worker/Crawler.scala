package worker

import models.{Work, WorkType, WorkTable}
import play.api.{Logger, Play}
import play.api.Play.current
import play.api.libs.ws.WS

class Crawler {

  def register(): Boolean = {
    Play.configuration.getString("dtc.master.ip").exists {
      masterIp =>
        val myUrl = Play.configuration.getString("http.address").getOrElse("127.0.0.1") + Play.configuration.getInt("http.port").getOrElse(9000)
        val url = masterIp + controllers.routes.MasterController.addWorker(myUrl)
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
