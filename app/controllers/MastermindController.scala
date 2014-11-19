package controllers

import master.Mastermind
import play.api.libs.json.Json
import play.api.mvc._

object MastermindController extends Controller {

  def addWorker(ip: String) = Action {
    val worker = Mastermind.registerWorker(ip)
    Mastermind.assignWork(worker.id)
    Ok(Json.obj("success" -> "ok"))
  }

  def workDone(wid: Long) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}