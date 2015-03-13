
package controllers

import play.api._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model._
import play.api.libs.json.Json

object Portal extends Controller {


  def index() =  Action {

      //Ok(views.html.DBTest(DBManager.test))
      implicit val fooFormat = Json.writes[Category]
    implicit val foobarFormat = Json.writes[WebBasedSeller]
    implicit val barfooFormat = Json.writes[CustomerReview]
    implicit val expertFormat = Json.writes[ExpertReview]
    implicit val reviewFormat = Json.writes[Review]
    implicit val postingFormat = Json.writes[Posting]

        implicit val barFormat = Json.writes[Product]

        Ok(Json.toJson(DBManager.retrieveProduct(Map("UPC" -> "2555"))))
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