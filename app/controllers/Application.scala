package controllers

import models.traits.Filter
import models.{HashtagTable, UserDataTable, UserTable, UserTweetTable, _}
import play.api.mvc.{Action, Controller}

case class Stats(tweetsPerUser: Long, followersPerUser: Long, friendsPerUser: Long)

object Application extends Controller {

  def index = Action {
    implicit request =>
      Redirect(controllers.routes.Application.tweets(None, None, None, None))
  }

  def tweets(step: Option[Int], hashtag: Option[String], username: Option[String], location: Option[String]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val tweets = (hashtag, username, location) match {
        case (Some(hashtagFilter), _, _) =>
          UserTweetTable.listHashtag(hashtagFilter, filter.limit, filter.offset)
        case (_, Some(usernameFilter), _) =>
          UserTweetTable.listUsername(usernameFilter, filter.limit, filter.offset)
        case (_, _, Some(locationFilter)) =>
          UserTweetTable.listLocation(locationFilter, filter.limit, filter.offset)
        case _ =>
          UserTweetTable.listOrdered(filter.limit, filter.offset)
      }
      Ok(views.html.index(tweets, filter))
  }

  def tweetsByHashtag(hashtag: String, step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val tweets = UserTweetTable.listHashtag(hashtag, filter.limit, filter.offset)
      Ok(views.html.index(tweets, filter))
  }

  def tweetsByUser(username: String, step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val tweets = UserTweetTable.listUsername(username, filter.limit, filter.offset)
      Ok(views.html.index(tweets, filter))
  }

  def tweetsByLocation(location: String, step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val tweets = UserTweetTable.listLocation(location, filter.limit, filter.offset)
      Ok(views.html.index(tweets, filter))
  }

  def hashtags(step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val hashtags = HashtagTable.listCount(filter.limit, filter.offset)
      Ok(views.html.hashtags(hashtags, filter))
  }

  def users(step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val users = UserTweetTable.listCount(filter.limit, filter.offset)
      Ok(views.html.users(users, filter))
  }

  def locations(step: Option[Int]) = Action {
    implicit request =>
      val filter = Filter(limit = 20, step.getOrElse(0))
      val locations = LocationTable.listCount(filter.limit, filter.offset)
      Ok(views.html.locations(locations, filter))
  }

  def stats = Action {
    implicit request =>
      val usersCount = UserTable.count()
      val followersCount = UserDataTable.countFollowers()
      val friendsCount = UserDataTable.countFriends()
      val tweetsCount = UserTweetTable.count()

      val tweetsPerUser = tweetsCount / Math.max(1, tweetsCount)
      val followersPerUser = followersCount / Math.max(1, usersCount)
      val friendsPerUser = friendsCount / Math.max(1, usersCount)

      val stats = Stats(tweetsPerUser, followersPerUser, friendsPerUser)

      Ok(views.html.stats(stats))
  }

}