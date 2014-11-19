import master.Mastermind
import play.api.Play.current
import play.api.{Application, GlobalSettings, Logger, Play}
import worker.Crawler

object Global extends GlobalSettings {

  private lazy val config = Play.configuration

  override def onStart(app: Application) {
    Logger.info("Distributed Twitter Client is running")

    if (config.getBoolean("dtc.mastermind.isActive").exists(identity)) {
      Mastermind.createWork()
      Mastermind.assignWork()
    }

    if (config.getBoolean("dtc.worker.isActive").exists(identity)){
      val crawler = new Crawler
      crawler.register()
    }

  }

}
