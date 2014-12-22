package models

import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class User(id: Long, username: String, timestamp: DateTime, lastUpdate: DateTime, nrUpdates: Long)

class UserTableDef(tag: Tag) extends Table[User](tag, "User") with BaseTable[User] {

  lazy val username = column[String]("username", O.NotNull)
  lazy val timestamp = column[DateTime]("timestamp", O.NotNull)
  lazy val lastUpdate = column[DateTime]("lastUpdate", O.NotNull)
  lazy val nrUpdates = column[Long]("nrUpdates", O.NotNull)

  def * = (id, username, timestamp, lastUpdate, nrUpdates) <>(User.tupled, User.unapply)

}

object UserTable extends TableQuery(new UserTableDef(_)) with BaseTableQueryOps[UserTableDef, User] {
  self =>

  lazy val db = GenericDB

  def listUsersToCrawl: Seq[User] = {
    db.withSession {
      (for {
        (user, data) <- this.leftJoin(UserDataTable).on(_.id === _.userId)
      } yield (user, data))
        .filter { case (user, _) => user.lastUpdate < DateTime.now().minusMinutes(15)}
        .sortBy { case (_, data) => (data.followers.desc, data.friends.desc)}
        .map { case (user, _) => user}
        .list
    }
  }

  def updateLastUpdate(userId: Long): Boolean = {
    db.withSession {
      self.filter(_.id === userId).map(_.lastUpdate).update(DateTime.now()) > 0
    }
  }

}
