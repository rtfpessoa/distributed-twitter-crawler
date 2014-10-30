package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class UserTweet(id: Long, userId: Long, tweet: String)

class UserTweetTableDef(tag: Tag) extends Table[UserTweet](tag, "UserTweet") with BaseTable[UserTweet] {

  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val tweet = column[String]("tweet", O.NotNull)

  def * = (id, userId, tweet) <>(UserTweet.tupled, UserTweet.unapply)

}

class UserTweetTable extends TableQuery(new UserTweetTableDef(_)) with BaseTableQueryOps[UserTweetTableDef, UserTweet] {
  self =>

  lazy val db = GenericDB

}
