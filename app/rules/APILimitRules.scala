package rules

import models.{APILimit, APILimitTable, Work, WorkType}
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current

object APILimitRules {

  private lazy val config = Play.configuration

  def isLimited(work: Work): Boolean = {
    val defaultWindow = APILimit(-1, "", DateTime.now().minusDays(1), 0)

    val endpoints = work.workType match {
      case WorkType.Tweet => Seq("statuses/user_timeline")
      case WorkType.UserProfile => Seq("friends/list", "followers/list")
    }

    val limitViolation = endpoints.map {
      endpoint =>
        val maxRequests = config.getInt(s"dtc.limit.$endpoint").getOrElse(0)
        val currentWindow = APILimitTable.getLatestWindow(endpoint).getOrElse(defaultWindow)

        val window = if (currentWindow.windowStart.plusMinutes(15).isAfterNow) {
          currentWindow
        } else {
          APILimitTable.create(APILimit(-1, endpoint, DateTime.now, 0))
        }

        window.windowStart.plusMinutes(15).isAfterNow && window.requests < maxRequests
    }

    limitViolation.contains(false)
  }

  def withAPILimit[A](endpoint: String)(block: => Option[A]): Option[A] = {
    APILimitTable.getLatestWindow(endpoint).map {
      window =>
        APILimitTable.update(window.copy(requests = window.requests + 1))
        block
    }.flatten
  }
}