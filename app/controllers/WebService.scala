package controllers

import play.api.mvc.Controller
import play.api._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model._
import play.api.libs.json.{Json, JsError}
/**
 * Created by kkk on 3/8/2015.
 */
object WebService extends Controller{

  def searchProduct(name : String) = Action{

    Ok(Json.toJson(APP_DBManager.searchProducts(name)))
  }


  def getProduct(code : String, codeType : String) = Action{

    Ok(Json.toJson(APP_DBManager.retrieveProduct(Map(codeType -> code))))
  }


  def insertProduct() =
    Action {
      response =>
        val k = Json.parse(response.body.asText.getOrElse("none"))
        val kk = k.validate[DataCollectionModel.WebSeller]

      Ok(views.html.DBTest(kk.get.toString))
    }
  }



