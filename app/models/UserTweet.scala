package models

import api.{Location, Tweet}
import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import play.api.libs.json.{Format, Json}

import scala.slick.driver.PostgresDriver.simple._

case class UserTweet(id: Long, userId: Long, tweet: Tweet) {
  val user = UserTable.getById(userId)
}

trait TweetTableMappers {
  implicit val apiLocationFormater: Format[Location] = Json.format[Location]
  implicit val apiTweetFormater: Format[Tweet] = Json.format[Tweet]

  implicit val tweetMapper = MappedColumnType.base[Tweet, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[Tweet])
}

class UserTweetTableDef(tag: Tag) extends Table[UserTweet](tag, "UserTweet") with BaseTable[UserTweet] with TweetTableMappers {

  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val tweet = column[Tweet]("tweet", O.NotNull)

  def * = (id, userId, tweet) <>(UserTweet.tupled, UserTweet.unapply)

}

object UserTweetTable extends TableQuery(new UserTweetTableDef(_)) with BaseTableQueryOps[UserTweetTableDef, UserTweet] with TweetTableMappers {
  self =>

  lazy val db = GenericDB

}
