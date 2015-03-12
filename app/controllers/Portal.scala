
package controllers

import play.api._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model.{DBManager, User}


object Portal extends Controller {


  def index() =  Action {

      Ok(views.html.DBTest(DBManager.test))
    }


/*
  def login() {
    val userFormConstraints2 = Form(
      mapping(
        "pwd" -> nonEmptyText,
        "usr" -> nonEmptyText
      )(User.apply)(User.unapply)
    )


  }

  def loginSubmit() {

  }

*/


}