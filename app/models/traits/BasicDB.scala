package models.traits

import java.sql.SQLException

import play.api.Logger

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
        val stack = Thread.currentThread().getStackTrace.toSeq
          .filter(_.getClassName.contains("model"))
          .filterNot(_.getClassName.contains("model.traits")).drop(1).head
        val method = s"${stack.getClassName}:${stack.getMethodName}:${stack.getLineNumber}"
        Logger.warn(s"Concurrent update on $method. $retries retries left")
        withRetry(block, retries - 1)
      case Failure(e: SQLException) =>
        Logger.logger.error("Exception information:", e)
        Logger.logger.error("Exception information:", e.getNextException)
        throw e
      case Failure(e) =>
        Logger.logger.error("Exception information:", e)
        throw e
    }
  }

}
