package models

import api.{Tweet, TweetLocation}
import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class UserTweet(id: Long, twitterId: Long, userId: Long, tweet: Tweet, timestamp: DateTime) {
  val user = UserTable.getById(userId)
}

trait TweetTableMappers {
  implicit val apiLocationFormatter: Format[TweetLocation] = Json.format[TweetLocation]
  implicit val apiTweetFormatter: Format[Tweet] = Json.format[Tweet]

  implicit val tweetMapper = MappedColumnType.base[Tweet, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[Tweet])
}

class UserTweetTableDef(tag: Tag) extends Table[UserTweet](tag, "UserTweet") with BaseTable[UserTweet] with TweetTableMappers {

  lazy val twitterId = column[Long]("twitterId", O.NotNull)
  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val tweet = column[Tweet]("tweet", O.NotNull)
  lazy val timestamp = column[DateTime]("timestamp", O.NotNull)

  def * = (id, twitterId, userId, tweet, timestamp) <>(UserTweet.tupled, UserTweet.unapply)

}

object UserTweetTable extends TableQuery(new UserTweetTableDef(_)) with BaseTableQueryOps[UserTweetTableDef, UserTweet] with TweetTableMappers {
  self =>

  lazy val db = GenericDB

  def listHashtag(filter: String, limit: Int, offset: Int) = {
    db.withSession {
      (for {
        hashtag <- HashtagTable if hashtag.label like "%" + filter + "%"
        userTweet <- this if userTweet.id === hashtag.tweetId
      } yield userTweet).drop(offset).take(limit).list
    }
  }

  def listUsername(filter: String, limit: Int, offset: Int) = {
    db.withSession {
      (for {
        user <- UserTable if user.username like "%" + filter + "%"
        userTweet <- this if userTweet.userId === user.id
      } yield userTweet).drop(offset).take(limit).list
    }
  }

  def listLocation(filter: String, limit: Int, offset: Int) = {
    db.withSession {
      (for {
        location <- LocationTable if location.label like "%" + filter + "%"
        userTweet <- this if userTweet.id === location.tweetId
      } yield userTweet).drop(offset).take(limit).list
    }
  }

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

  def getMaxTwitterId(userId: Long): Option[Long] = {
    db.withSession {
      self.filter(_.userId === userId).map(_.twitterId).max.run
    }
  }

}
