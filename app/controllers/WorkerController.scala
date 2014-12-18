package controllers

import java.net.InetAddress

import models.WorkerTable
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import worker.Crawler

object WorkerController extends Controller {

  private lazy val config = Play.configuration

  def newWork(workId: Long) = Action {
    val myIp = Option(InetAddress.getLocalHost.getHostAddress)
    val myUrl = s"http://${myIp.getOrElse("127.0.0.1")}:${config.getInt("http.port").getOrElse(9000)}"
    WorkerTable.getByIp(myUrl).map {
      worker =>
        WorkerTable.update(worker.copy(heartbeat = DateTime.now()))
    }

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
