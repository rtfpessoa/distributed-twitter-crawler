package controllers

import models.UserTweetTable
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    val tweets = UserTweetTable.list()
    Ok(views.html.index(tweets))
  }

}