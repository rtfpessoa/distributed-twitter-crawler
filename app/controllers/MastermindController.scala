package controllers

import master.Mastermind
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

object MastermindController extends Controller {

  def addWorker(ip: String) = Action {
    val worker = Mastermind.registerWorker(ip)

    Logger.info(s"$ip registered as worker ${worker.id}.")

    Mastermind.assignWork(worker.id).map {
      work =>
        Mastermind.sendWork(worker.id, work.id)
        Ok(Json.obj("success" -> "ok"))
    }.getOrElse(Ok(Json.obj("error" -> "not authorized")))
  }

  def workDone(workerId: Long) = Action {
    Logger.info(s"Worker $workerId finished a job.")

    Mastermind.createWork()
    Mastermind.assignWork(workerId).map {
      work =>
        Mastermind.sendWork(workerId, work.id)
        Ok(Json.obj("success" -> "ok"))
    }.getOrElse(Ok(Json.obj("error" -> "not authorized")))
  }

}