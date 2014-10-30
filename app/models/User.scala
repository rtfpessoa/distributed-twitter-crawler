package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class User(id: Long, username: String)

class UserTableDef(tag: Tag) extends Table[User](tag, "User") with BaseTable[User] {

  lazy val username = column[String]("username", O.NotNull)

  def * = (id, username) <>(User.tupled, User.unapply)

}

class UserTable extends TableQuery(new UserTableDef(_)) with BaseTableQueryOps[UserTableDef, User] {
  self =>

  lazy val db = GenericDB

}
