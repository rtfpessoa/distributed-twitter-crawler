package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

object WorkState extends Enumeration {
  val New, Completed = Value
}

case class Work(id: Long, url: String, state: WorkState.Value)

trait WorkStateMapper {
  implicit val workStateMapper = MappedColumnType.base[WorkState.Value, String](
    _.toString, WorkState.withName
  )
}

class WorkTableDef(tag: Tag) extends Table[Work](tag, "Work") with BaseTable[Work] with WorkStateMapper {

  lazy val url = column[String]("url", O.NotNull)
  lazy val state = column[WorkState.Value]("workId", O.NotNull)

  def * = (id, url, state) <>(Work.tupled, Work.unapply)

}

class WorkTable extends TableQuery(new WorkTableDef(_)) with BaseTableQueryOps[WorkTableDef, Work] with WorkStateMapper {
  self =>

  lazy val db = GenericDB

  def get(projectId: Long): Seq[Work] = {
    db.withSession {
      self.list()
    }
  }

}
