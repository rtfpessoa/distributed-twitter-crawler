package controllers

import play.api.mvc._

object MasterController extends Controller {

  def addWorker(ip: String) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def workDone(wid: Long) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}