package models

import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._

case class User(id: Long, username: String, timestamp: DateTime)

class UserTableDef(tag: Tag) extends Table[User](tag, "User") with BaseTable[User] {

  lazy val username = column[String]("username", O.NotNull)
  lazy val timestamp = column[DateTime]("timestamp", O.NotNull)

  def * = (id, username, timestamp) <>(User.tupled, User.unapply)

}

object UserTable extends TableQuery(new UserTableDef(_)) with BaseTableQueryOps[UserTableDef, User] {
  self =>

  lazy val db = GenericDB

}
