package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class WorkResult(id: Long, workId: Long, result: String)

class WorkResultTableDef(tag: Tag) extends Table[WorkResult](tag, "WorkResult") with BaseTable[WorkResult] {

  lazy val workId = column[Long]("workId", O.NotNull)
  lazy val result = column[String]("result", O.NotNull)

  def * = (id, workId, result) <>(WorkResult.tupled, WorkResult.unapply)

}

class WorkResultTable extends TableQuery(new WorkResultTableDef(_)) with BaseTableQueryOps[WorkResultTableDef, WorkResult] {
  self =>

  lazy val db = GenericDB

  def get(projectId: Long): Seq[WorkResult] = {
    db.withSession {
      self.list()
    }
  }

}
