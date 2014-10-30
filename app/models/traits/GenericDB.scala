package models.traits

import play.api.Play.current
import play.api.db.DB

import scala.slick.driver.PostgresDriver.simple.Database

object GenericDB extends BasicDB {

  private val dataSource = DB.getDataSource()
  override protected val database = Database.forDataSource(dataSource)

}
