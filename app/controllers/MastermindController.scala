package controllers

import master.Mastermind
import play.api.libs.json.Json
import play.api.mvc._

object MastermindController extends Controller {

  def addWorker(ip: String) = Action {
    val worker = Mastermind.registerWorker(ip)
    Mastermind.assignWork(worker.id).map {
      work =>
        Mastermind.sendWork(worker.id, work.id)
        Ok(Json.obj("success" -> "ok"))
    }.getOrElse(Ok(Json.obj("error" -> "not authorized")))
  }

  def workDone(wid: Long) = Action {
    Ok(Json.obj("success" -> "ok"))
  }

}