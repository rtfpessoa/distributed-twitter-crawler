package master

import models._
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws.WS
import rules.APILimitRules

import scala.util.Try

object Mastermind {

  def createWork(): Boolean = {
    Logger.info(s"Creating new work.")

    val allWork = WorkTable.list()
    val allUsers = UserTable.list()

    allUsers.collect {
      case user if !allWork.exists(_.userId == user.id) =>
        WorkTable.create(Work(-1, None, WorkType.UserProfile, user.id, WorkState.New, None))
        WorkTable.create(Work(-1, None, WorkType.Tweet, user.id, WorkState.New, None))
    }.nonEmpty
  }

  def assignWork(): Boolean = {
    Logger.info(s"Assigning work.")

    val allWorkers = WorkerTable.list()
    val allWork = WorkTable.list()

    allWorkers.collect {
      case worker if allWork.exists(w => !w.workerId.contains(worker.id) &&
        !Seq(WorkState.New, WorkState.Working).contains(w.state)) =>
        assignWork(worker.id)
    }.nonEmpty
  }

  def assignWork(workerId: Long): Option[Work] = {
    Logger.info(s"Assigning work to Worker $workerId.")

    Try {
      WorkTable.listByState(WorkState.New).collectFirst {
        case work if !APILimitRules.isLimited(work) =>
          WorkTable.update(work.copy(workerId = Option(workerId)))
          work
      }
    }.toOption.flatten
  }

  def registerWorker(ip: String): Worker = {
    Logger.info(s"Registering worker from $ip.")
    WorkerTable.create(Worker(-1, ip))
  }

  def sendWork(workerId: Long, workId: Long): Unit = {
    Logger.info(s"Sending work $workId to worker $workerId.")
    WorkerTable.getById(workerId).map {
      worker =>
        WS.url(s"${worker.ip}${controllers.routes.WorkerController.newWork(workId)}").get()
    }
  }

}
