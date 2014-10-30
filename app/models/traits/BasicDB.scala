package models.traits

import java.sql.SQLException

import scala.slick.driver.PostgresDriver.simple._
import scala.util._

trait BasicDB {

  protected def database: Database

  def withSession[A](block: => A): A = withRetry {
    database.withDynSession {
      block
    }
  }

  def transaction[A](block: => A): A = withRetry {
    database.withDynTransaction {
      block
    }
  }

  private def withRetry[A](block: => A, retries: Int = 3): A = {
    Try(block) match {
      case Success(result) => result
      case Failure(ex: SQLException) if retries > 0 =>
        withRetry(block, retries - 1)
      case Failure(e: SQLException) =>
        throw e
      case Failure(e) =>
        throw e
    }
  }

}
