package rules

import models.{APILimit, APILimitTable}
import org.joda.time.{DateTime, Duration}
import play.api.Play
import play.api.Play.current

object APILimitRules {

  private lazy val config = Play.configuration
  private lazy val defaultWindowDuration = Duration.standardMinutes(15)

  def withAPILimit[A](endpoint: String)(block: => Option[A]): Option[A] = {
    val maxRequests = config.getInt(s"dtc.limit.$endpoint").getOrElse(0)

    APILimitTable.getLatestWindow(endpoint).flatMap {
      currentWindow =>
        val window = if (currentWindow.windowStart.plus(defaultWindowDuration).isAfterNow) {
          currentWindow
        } else {
          APILimitTable.create(APILimit(-1, endpoint, DateTime.now, 0))
        }

        if (window.requests < maxRequests) {
          Some(block)
        } else {
          None
        }
    }.flatten
  }

}
