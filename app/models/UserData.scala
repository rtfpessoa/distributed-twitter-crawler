package models

import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class UserData(id: Long, userId: Long, followers: Long, friends: Long)

class UserDataTableDef(tag: Tag) extends Table[UserData](tag, "UserData") with BaseTable[UserData] {

  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val followers = column[Long]("followers", O.NotNull)
  lazy val friends = column[Long]("friends", O.NotNull)

  def * = (id, userId, followers, friends) <>(UserData.tupled, UserData.unapply)

}

object UserDataTable extends TableQuery(new UserDataTableDef(_)) with BaseTableQueryOps[UserDataTableDef, UserData] {
  self =>

  lazy val db = GenericDB

  def countFollowers(): Long = {
    db.withSession {
      self.map(_.followers).sum.run.getOrElse(0L)
    }
  }

  def countFriends(): Long = {
    db.withSession {
      self.map(_.friends).sum.run.getOrElse(0L)
    }
  }
}
