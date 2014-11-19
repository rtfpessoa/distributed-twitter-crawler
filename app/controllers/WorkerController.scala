package controllers

import play.api.libs.json.Json
import play.api.mvc._
import worker.Crawler

object WorkerController extends Controller {

  def newWork(wid: Long) = Action {
    Crawler.newWork(wid)
    Ok(Json.obj("success" -> "ok"))
  }

}