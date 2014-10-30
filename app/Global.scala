import play.api.{Application, GlobalSettings, Logger}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")

    //    val conf = app.configuration
    //TODO: for each type (mastermind, worker) start the app
  }

}
