package models

import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class Location(id: Long, tweetId: Long, label: String) {
  val tweet = UserTweetTable.getById(tweetId)
}

class LocationTableDef(tag: Tag) extends Table[Location](tag, "Location") with BaseTable[Location] {

  lazy val tweetId = column[Long]("tweetId", O.NotNull)
  lazy val label = column[String]("label", O.NotNull)

  def * = (id, tweetId, label) <>(Location.tupled, Location.unapply)

}

object LocationTable extends TableQuery(new LocationTableDef(_)) with BaseTableQueryOps[LocationTableDef, Location] {
  self =>

  lazy val db = GenericDB

  def listCount(limit: Int, offset: Int): Seq[(String, Int)] = {
    db.withSession {
      self.groupBy(_.label).map{
        case (t1, t2) =>
          t1 -> t2.size
      }.sortBy(_._2.desc).drop(offset).take(limit).list
    }
  }

}
