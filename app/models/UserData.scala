package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class UserData(id: Long, userId: Long, followers: Int, following: Int)

class UserDataTableDef(tag: Tag) extends Table[UserData](tag, "UserData") with BaseTable[UserData] {

  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val followers = column[Int]("followers", O.NotNull)
  lazy val following = column[Int]("following", O.NotNull)

  def * = (id, userId, followers, following) <>(UserData.tupled, UserData.unapply)

}

class UserDataTable extends TableQuery(new UserDataTableDef(_)) with BaseTableQueryOps[UserDataTableDef, UserData] {
  self =>

  lazy val db = GenericDB

}
