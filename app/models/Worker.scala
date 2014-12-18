package models

import models.traits.{BaseTable, BaseTableQueryOps, GenericDB}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class Worker(id: Long, ip: String, heartbeat: DateTime)

class WorkerTableDef(tag: Tag) extends Table[Worker](tag, "Worker") with BaseTable[Worker] {

  lazy val ip = column[String]("ip", O.NotNull)
  lazy val heartbeat = column[DateTime]("heartbeat", O.NotNull)

  def * = (id, ip, heartbeat) <>(Worker.tupled, Worker.unapply)

}

object WorkerTable extends TableQuery(new WorkerTableDef(_)) with BaseTableQueryOps[WorkerTableDef, Worker] {
  self =>

  lazy val db = GenericDB

  def getByIp(ip: String): Option[Worker] = {
    db.withSession {
      self.filter(_.ip === ip).firstOption
    }
  }

}
