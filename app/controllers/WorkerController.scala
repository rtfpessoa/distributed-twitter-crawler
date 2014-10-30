package controllers

import play.api.mvc._

object WorkerController extends Controller {

  def newWork(wid: Long) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}