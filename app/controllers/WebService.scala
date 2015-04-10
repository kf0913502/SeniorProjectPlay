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

    Ok(Json.toJson(APP_DBManager.searchProducts(name)) )
  }


  def getProduct(code : String, codeType : String) = Action{
    val result = APP_DBManager.retrieveProduct(Map(codeType -> code))
    if (result != null)
      Ok(Json.toJson(result))
    else 
      NotFound("")
  }


  def insertProduct() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.Product]

      Ok({DataCollection_DBManager.insertWebSellerProduct(modelJsonObject.get); "ok"})
    }

  def insertWebPosting() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.WebPosting]

        Ok({DataCollection_DBManager.insertWebPosting(modelJsonObject.get); "OK"})
    }

  def insertWebImage() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.ProductImage]

        Ok({DataCollection_DBManager.insertWebSellerImage(modelJsonObject.get); "OK"})
    }

  def insertWebDesc() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.Desc]

        Ok({DataCollection_DBManager.insertWebSellerDesc(modelJsonObject.get); "OK"})
    }

  def insertWebSeller() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.WebSeller]

        Ok({DataCollection_DBManager.insertWebSeller(modelJsonObject.get); "OK"})
    }

  def insertWebOffer() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.WebOffer]

        Ok({DataCollection_DBManager.insertOffer(modelJsonObject.get); "OK"})
    }

  def insertCustomerReview() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.CustomerReview]

        Ok({DataCollection_DBManager.insertCustomerReview(modelJsonObject.get); "OK"})
    }

  def insertExpertReview() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.ExpertReview]

        Ok({DataCollection_DBManager.insertExpertReview(modelJsonObject.get); "OK"})
    }


  def test() =
  Action{
    response =>
      DataCollection_DBManager.insertWebSellerProduct(DataCollectionModel.Product(Map("UPC" -> "2567", "NPN" -> "2554"),"Iphone", "phones" ))
      Ok("")
  }


  def insertRelated() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.Related]
        Ok({DataCollection_DBManager.insertRelated(modelJsonObject.get);"OK"})
    }


  def insertQuestion() =
    Action {
      response =>
        val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
        val modelJsonObject = parsedJson.validate[DataCollectionModel.Question]
        Ok({DataCollection_DBManager.insertQuestion(modelJsonObject.get);"OK"})
    }

  def insertWebPriceReduction()=
  Action{
    response =>
      val parsedJson = Json.parse(response.body.asText.getOrElse("none"))
      val modelJsonObject = parsedJson.validate[DataCollectionModel.WebPriceReduction]
      Ok({DataCollection_DBManager.insertWebPriceReduction(modelJsonObject.get);"OK"})
  }

  def getAllCategories()=
    Action{

      Ok(Json.toJson(APP_DBManager.retrieveAllCategories()) )
    }

  def getOffersInCategory(name : String)=
    Action{
      Ok(Json.toJson(APP_DBManager.retrieveOffersInCategory(name)) )
    }
  }



