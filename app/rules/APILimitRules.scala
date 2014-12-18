package rules

import models.{WorkType, APILimit, APILimitTable, Work}
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current

object APILimitRules {

  private lazy val config = Play.configuration

  def withAPILimit[A](endpoint: String)(block: => Option[A]): Option[A] = {
    val defaultWindow = APILimit(-1, "", DateTime.now().minusDays(1), 0)

    val maxRequests = config.getInt(s"dtc.limit.$endpoint").getOrElse(0)

    val currentWindow = APILimitTable.getLatestWindow(endpoint).getOrElse(defaultWindow)

    val window = if (currentWindow.windowStart.plusMinutes(15).isAfterNow) {
      currentWindow
    } else {
      APILimitTable.create(APILimit(-1, endpoint, DateTime.now, 0))
    }

    if (window.requests <= maxRequests) {
      APILimitTable.update(currentWindow.copy(requests = currentWindow.requests + 1))
      block
    } else {
      None
    }
  }

  def withAPILimit(work: Work)(block: => Work): Option[Work] = {
    val defaultWindow = APILimit(-1, "", DateTime.now().minusMinutes(1), 0)

    val endpoints = work.workType match {
      case WorkType.Tweet => Seq("statuses/user_timeline")
      case WorkType.UserProfile => Seq("friends/list", "followers/list")
    }

    val limitViolation = endpoints.map {
      endpoint =>
        val maxRequests = config.getInt(s"dtc.limit.$endpoint").getOrElse(0)
        val currentWindow = APILimitTable.getLatestWindow(endpoint).getOrElse(defaultWindow)

        currentWindow.windowStart.plusMinutes(15).isAfterNow && currentWindow.requests <= maxRequests
    }

    if (!limitViolation.contains(false)) {
      Some(block)
    } else {
      None
    }
  }

}
