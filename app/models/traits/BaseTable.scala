package models.traits

import java.sql.Timestamp

import org.joda.time.DateTime

import scala.language.reflectiveCalls
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.{Column, ColumnOrdered}

object Types {

  type IDObj = AnyRef {def id: Long}

  type BaseTableT[A <: IDObj] = Table[A] {
    def id: Column[Long]
  }

}

trait BaseTableMappers {

  implicit val timestampMapper = MappedColumnType.base[DateTime, Timestamp](
    datetime => new Timestamp(datetime.getMillis),
    new DateTime(_)
  )

}

trait BaseTable[A <: Types.IDObj] extends BaseTableMappers {
  self: Table[A] =>

  lazy val id = column[Long]("id", O.PrimaryKey, O.AutoInc)

}

trait BaseTableQueryOps[SimpleTable <: Types.BaseTableT[A], A <: Types.IDObj] extends BaseTableMappers {
  self: TableQuery[SimpleTable] =>

  implicit def db: BasicDB

  def getById(id: Long): Option[A] = {
    db.withSession {
      self.filter(_.id === id).firstOption
    }
  }


  def listByIds(ids: Seq[Long]): Seq[A] = {
    db.withSession {
      (for (obj <- self if obj.id inSetBind ids) yield obj).list
    }
  }


  def list(): List[A] = {
    db.withSession {
      (for (obj <- self sortBy (_.id)) yield obj).list
    }
  }

  def deleteById(ids: Seq[Long]): Boolean = {
    db.withSession {
      self.filter(_.id inSet ids).delete > 0
    }
  }

  def deleteById(id: Long): Boolean = deleteById(Seq(id))

  def update(objectToUpdate: A) =
    db.withSession {
      self.filter(_.id === objectToUpdate.id).update(objectToUpdate)
    }

  def create(objectToCreate: A): A = {
    db.withSession {
      (self returning self) += objectToCreate
    }
  }

  def create(objectsToCreate: Seq[A]): Unit = {
    db.withSession {
      self ++= objectsToCreate
    }
  }

  def createAndReturn(objectsToCreate: Seq[A]): Seq[A] = {
    db.withSession {
      (self returning self) ++= objectsToCreate
    }
  }

}
