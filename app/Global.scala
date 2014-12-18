import master.Mastermind
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings, Logger, Play}
import worker.Crawler

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

  private lazy val config = Play.configuration

  override def onStart(app: Application) {
    Logger.info("Distributed Twitter Client is running")

    if (config.getBoolean("dtc.mastermind.isActive").exists(identity)) {
      Mastermind.createWork()
      Mastermind.assignWork()

      val workerDelayDuration = Duration(config.getInt("dtc.work.delay").getOrElse(30), SECONDS)
      val workerIntervalDuration = Duration(config.getInt("dtc.work.interval").getOrElse(30), SECONDS)
      Akka.system.scheduler.schedule(workerDelayDuration, workerIntervalDuration) {
        Mastermind.assignWork()
      }
    }

    if (config.getBoolean("dtc.worker.isActive").exists(identity)) {
      Crawler.register()
    }

  }

}
