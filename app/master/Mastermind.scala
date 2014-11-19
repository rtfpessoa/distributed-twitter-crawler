package master

import models._
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._
import scala.util.control.NonFatal

class Mastermind {

  private lazy val config = Play.configuration

  def createWork(): Boolean = {
    val allWork = WorkTable.list()
    val allUsers = UserTable.list()

    allUsers.collect {
      case user if !allWork.exists(_.userId == user.id) =>
        WorkTable.create(Work(-1, None, WorkType.UserProfile, user.id, WorkState.New, None))
        WorkTable.create(Work(-1, None, WorkType.Tweet, user.id, WorkState.New, None))
    }.nonEmpty
  }

  def assignWork(): Boolean = {
    val allWorkers = WorkerTable.list()
    val allWork = WorkTable.list()

    allWorkers.collect {
      case worker if !allWork.exists(_.workerId.contains(worker.id)) =>
        assignWork(worker.id)
    }.nonEmpty
  }

  def assignWork(workerId: Long): Boolean = {
    val result = try {
      WorkTable.listByState(WorkState.New).headOption.map {
        work =>
          WorkTable.update(work.copy(workerId = Option(workerId)))
      }.nonEmpty
    } catch {
      case NonFatal(_) =>
        assignWork(workerId)
    }

    val workerDelayDuration = Duration(config.getInt("dtc.work.delay").getOrElse(30), SECONDS)
    Akka.system.scheduler.scheduleOnce(workerDelayDuration) {
      assignWork()
    }

    result
  }

}
