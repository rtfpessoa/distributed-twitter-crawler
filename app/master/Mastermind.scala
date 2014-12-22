package master

import models._
import org.joda.time.DateTime
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws.WS
import rules.APILimitRules

import scala.util.Try

object Mastermind {

  def maintainWorkers(): Boolean = {
    Logger.info(s"Maintaining workers.")

    val allWork = WorkTable.list()
    val allWorkers = WorkerTable.list()

    allWorkers.collect {
      case worker if allWork.exists(_.workerId.contains(worker.id)) && worker.heartbeat.plusMinutes(1).isBeforeNow =>
        Logger.info(s"Removing worker ${worker.id}.")

        allWork.filter(_.workerId.contains(worker.id)).map {
          work =>
            WorkTable.update(work.copy(state = WorkState.Error))
        }

        WorkerTable.deleteById(worker.id)
    }.nonEmpty
  }

  def createWork(): Boolean = {
    Logger.info(s"Creating new work.")

    val allUsers = UserTable.listUsersToCrawl(Option(100))

    allUsers.map {
      user =>
        UserTable.updateLastUpdate(user.id)
        WorkTable.create(Seq(Work(-1, None, WorkType.UserProfile, user.id, WorkState.New, None),
          Work(-1, None, WorkType.Tweet, user.id, WorkState.New, None)))
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
          WorkerTable.getById(workerId).map { _ =>
            WorkTable.update(work.copy(workerId = Option(workerId)))
          }
          work
      }
    }.toOption.flatten.fold[Option[Work]] {
      Logger.info(s"No work for worker $workerId.")
      None
    }(w => Some(w))
  }

  def registerWorker(ip: String): Worker = {
    Logger.info(s"Registering worker from $ip.")
    WorkerTable.create(Worker(-1, ip, DateTime.now()))
  }

  def sendWork(workerId: Long, workId: Long): Unit = {
    Logger.info(s"Sending work $workId to worker $workerId.")
    WorkerTable.getById(workerId).map {
      worker =>
        WS.url(s"${worker.ip}${controllers.routes.WorkerController.newWork(workId)}").get()
    }
  }

}
