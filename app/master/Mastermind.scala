package master

import models._
import play.api.Play.current
import play.api.libs.ws.WS
import rules.APILimitRules._

import scala.util.Try

object Mastermind {

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
      case worker if allWork.exists(w => !w.workerId.contains(worker.id) &&
        !Seq(WorkState.New, WorkState.Working).contains(w.state)) =>
        assignWork(worker.id)
    }.nonEmpty
  }

  def assignWork(workerId: Long): Option[Work] =
    Try {
      WorkTable.listByState(WorkState.New).headOption.map {
        work =>
          withAPILimit(work) {
            WorkTable.update(work.copy(workerId = Option(workerId)))
            work
          }
      }.flatten
    }.toOption.flatten

  def registerWorker(ip: String): Worker = {
    WorkerTable.create(Worker(-1, ip))
  }

  def sendWork(workerId: Long, workId: Long): Unit = {
    WorkerTable.getById(workerId).map {
      worker =>
        WS.url(s"${worker.ip}${controllers.routes.WorkerController.newWork(workId)}").get()
    }
  }

}
