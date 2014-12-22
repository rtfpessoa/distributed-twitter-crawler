package models

import api.{Location, Tweet}
import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class UserTweet(id: Long, userId: Long, tweet: Tweet, timestamp: DateTime) {
  val user = UserTable.getById(userId)
}

trait TweetTableMappers {
  implicit val apiLocationFormatter: Format[Location] = Json.format[Location]
  implicit val apiTweetFormatter: Format[Tweet] = Json.format[Tweet]

  implicit val tweetMapper = MappedColumnType.base[Tweet, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[Tweet])
}

class UserTweetTableDef(tag: Tag) extends Table[UserTweet](tag, "UserTweet") with BaseTable[UserTweet] with TweetTableMappers {

  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val tweet = column[Tweet]("tweet", O.NotNull)
  lazy val timestamp = column[DateTime]("timestamp", O.NotNull)

  def * = (id, userId, tweet, timestamp) <>(UserTweet.tupled, UserTweet.unapply)

}

object UserTweetTable extends TableQuery(new UserTweetTableDef(_)) with BaseTableQueryOps[UserTweetTableDef, UserTweet] with TweetTableMappers {
  self =>

  lazy val db = GenericDB

  def listCount(limit: Int, offset: Int): Seq[(User, Int)] = {
    db.withSession {
      self.groupBy(_.userId).map {
        case (t1, t2) =>
          t1 -> t2.size
      }.sortBy(_._2.desc).drop(offset).take(limit).list.map {
        case (userId, count) =>
          UserTable.getById(userId).map {
            user =>
              (user, count)
          }
      }.flatten
    }
  }

}
