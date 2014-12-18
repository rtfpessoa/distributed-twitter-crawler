package rules

import models.{APILimit, APILimitTable}
import org.joda.time.{DateTime, Duration}
import play.api.Play
import play.api.Play.current

object APILimitRules {

  private lazy val config = Play.configuration
  private lazy val defaultWindowDuration = Duration.standardMinutes(15)
  private val defaultWindow = APILimit(-1, "", DateTime.now().minusDays(1), 0)

  def withAPILimit[A](endpoint: String)(block: => Option[A]): Option[A] = {
    val maxRequests = config.getInt(s"dtc.limit.$endpoint").getOrElse(0)

    val currentWindow = APILimitTable.getLatestWindow(endpoint).getOrElse(defaultWindow)

    val window = if (currentWindow.windowStart.plus(defaultWindowDuration).isAfterNow) {
      currentWindow
    } else {
      APILimitTable.create(APILimit(-1, endpoint, DateTime.now, 0))
    }

    if (window.requests < maxRequests) {
      block
    } else {
      None
    }

  }

}
