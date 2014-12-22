package controllers

import models.UserTweetTable
import models.traits.Filter
import play.api.mvc.{Action, Controller}

object Application extends Controller {

  def index = Action {
    implicit request =>
      Redirect(controllers.routes.Application.tweets(None))
  }

  def tweets(step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val tweets = UserTweetTable.list(filter.limit, filter.offset)
      Ok(views.html.index(tweets, filter))
  }

  def stats = Action {
    implicit request =>
      Ok(views.html.stats())
  }

}