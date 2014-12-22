package controllers

import models.traits.Filter
import models.{UserDataTable, UserTable, UserTweetTable}
import play.api.mvc.{Action, Controller}

case class Stats(tweetsPerUser: Long, followersPerUser: Long, friendsPerUser: Long)

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
      val users = UserTable.list()
      val usersData = UserDataTable.list()
      val tweets = UserTweetTable.list()

      val tweetsPerUser = users.length / Math.min(1, tweets.length)
      val followersPerUser = usersData.map(_.followers).sum / Math.min(1, users.length)
      val friendsPerUser = usersData.map(_.friends).sum / Math.min(1, users.length)

      val stats = Stats(tweetsPerUser, followersPerUser, friendsPerUser)

      Ok(views.html.stats(stats))
  }

}