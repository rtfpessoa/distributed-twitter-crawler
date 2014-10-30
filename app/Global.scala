import master.Mastermind
import play.api.Play.current
import play.api.{Application, GlobalSettings, Logger, Play}
import worker.Crawler

object Global extends GlobalSettings {

  private lazy val config = Play.configuration

  override def onStart(app: Application) {
    Logger.info("Distributed Twitter Clients is running")

    if (config.getBoolean("dtc.mastermind").exists(isMastermind => isMastermind)) {
      val mastermind = new Mastermind
      mastermind.createWork()
      mastermind.assignWork()
    } else {
      val crawler = new Crawler()
      //      crawler.register()
    }

  }

}
