package controllers

import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import worker.Crawler

import scala.concurrent.ExecutionContext.Implicits.global

object WorkerController extends Controller {

  def newWork(workId: Long) = Action {
    for {
      work <- Crawler.newWork(workId)
      workerId <- work.workerId
      masterIp <- Play.configuration.getString("dtc.mastermind.ip")
    } yield {
      val url = s"http://$masterIp${controllers.routes.MastermindController.workDone(workerId)}"
      WS.url(url).get()
    }

    Ok(Json.obj("success" -> "ok"))
  }

}
