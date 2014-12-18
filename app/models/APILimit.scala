package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class APILimit(id: Long, endpoint: String, windowStart: DateTime, requests: Int)

class APILimitTableDef(tag: Tag) extends Table[APILimit](tag, "APILimit") with BaseTable[APILimit] {

  lazy val endpoint = column[String]("endpoint", O.NotNull)
  lazy val windowStart = column[DateTime]("windowStart", O.NotNull)
  lazy val requests = column[Int]("requests", O.NotNull)

  def * = (id, endpoint, windowStart, requests) <>(APILimit.tupled, APILimit.unapply)

}

object APILimitTable extends TableQuery(new APILimitTableDef(_)) with BaseTableQueryOps[APILimitTableDef, APILimit] {
  self =>

  lazy val db = GenericDB

  def getLatestWindow(endpoint: String) = {
    db.withSession {
      self.filter(_.endpoint === endpoint).sortBy(_.windowStart.desc).firstOption
    }
  }

}
