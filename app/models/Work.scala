package models

import models.traits.{GenericDB, BaseTable, BaseTableQueryOps}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

object WorkState extends Enumeration {
  val New, Working, Completed = Value
}

object WorkType extends Enumeration {
  val Tweet, UserProfile = Value
}

case class Work(id: Long, workerId: Option[Long], workType: WorkType.Value, userId: Long, state: WorkState.Value, offset: Option[Int])

trait WorkStateMapper {
  implicit val workStateMapper = MappedColumnType.base[WorkState.Value, String](
    _.toString, WorkState.withName
  )
}

trait WorkTypeMapper {
  implicit val workTypeMapper = MappedColumnType.base[WorkType.Value, String](
    _.toString, WorkType.withName
  )
}

trait WorkTableMapper extends WorkStateMapper with WorkTypeMapper

class WorkTableDef(tag: Tag) extends Table[Work](tag, "Work") with BaseTable[Work] with WorkTableMapper {

  lazy val workerId = column[Option[Long]]("workerId", O.Nullable)
  lazy val workType = column[WorkType.Value]("workType", O.NotNull)
  lazy val userId = column[Long]("userId", O.NotNull)
  lazy val state = column[WorkState.Value]("workId", O.NotNull)
  lazy val offset = column[Option[Int]]("offset", O.NotNull)

  def * = (id, workerId, workType, userId, state, offset) <>(Work.tupled, Work.unapply)

}

object WorkTable extends TableQuery(new WorkTableDef(_)) with BaseTableQueryOps[WorkTableDef, Work] with WorkTableMapper {
  self =>

  lazy val db = GenericDB

}
