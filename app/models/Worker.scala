package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class Worker(id: Long, ip: String, workId: Option[Long])

class WorkerTableDef(tag: Tag) extends Table[Worker](tag, "Worker") with BaseTable[Worker] {

  lazy val ip = column[String]("ip", O.NotNull)
  lazy val workId = column[Option[Long]]("workId", O.Nullable)

  def * = (id, ip, workId) <>(Worker.tupled, Worker.unapply)

}

class WorkerTable extends TableQuery(new WorkerTableDef(_)) with BaseTableQueryOps[WorkerTableDef, Worker] {
  self =>

  lazy val db = GenericDB

  def get(projectId: Long): Seq[Worker] = {
    db.withSession {
      self.list()
    }
  }

}
