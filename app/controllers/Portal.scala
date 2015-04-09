
package controllers

import play.api._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model._
import play.api.libs.json.{Json, JsError}

//import model.jsonWrites._
object Portal extends Controller {


  def index() =  Action {
    /*DataCollection_DBManager.insertWebSeller(DataCollectionModel.WebSeller("www.amazon.com","amazon.png","Amazon Inc."))
    DataCollection_DBManager.insertWebSellerProduct(DataCollectionModel.Product(Map("UPC" -> "2555"),"Iphone", "phones" ))
    DataCollection_DBManager.insertWebSellerDesc(DataCollectionModel.Desc(Map("UPC" -> "2555"),"www.amazon.com", "Really Shiny"))
    DataCollection_DBManager.insertCustomerReview(DataCollectionModel.CustomerReview(Map("UPC" -> "2555"),"apple sucks","it does", "www.amazon.com"))
    DataCollection_DBManager.insertExpertReview(DataCollectionModel.ExpertReview(Map("UPC" -> "2555"),"www.lol.com/iphoneReview","Apple will fail","the lol page"))
    DataCollection_DBManager.insertWebPosting(DataCollectionModel.WebPosting(Map("UPC" -> "2555"),"5000","www.amazon.com","www.amazon.com/ihpones/asdasd"))
    DataCollection_DBManager.insertWebSellerImage(DataCollectionModel.ProductImage(Map("UPC" -> "2555"),"iphone.png","www.amazon.com"))
*/
    DataCollection_DBManager.insertOffer(DataCollectionModel.WebOffer(List(Map("UPC" -> "2555")),"www.amazon.com","200","2 for one","2013-03-03","2013-03-03","500"))
    Ok(views.html.DBTest("test"))
/*
    request =>
      val k = Json.parse("{\"info\":{\"codes\":{\"UPC\":\"2555\"},\"name\":\"iphone\",\"category\":{\"id\":\"\",\"parentId\":\"\",\"name\":\"phones\"},\"dateAdded\":\"2015-03-13 21:22:31.0\"},\"images\":[\"iphone.jpg\",\"iphone2.jpg\",\"iphone22.jpg\",\"iphone222.jpg\"],\"descriptions\":[\"Really fucking shiny22\"],\"webPosting\":[{\"price\":50000.0,\"seller\":{\"logo\":\"amazon.png\",\"URL\":\"www.Amazon.com\",\"id\":\"\"},\"URL\":\"www.amazon.com/iphoneLOL\"},{\"price\":50000.0,\"seller\":{\"logo\":\"amazon.png\",\"URL\":\"www.Amazon.com\",\"id\":\"\"},\"URL\":\"www.amazon.com/iphoneLOL\"}],\"localPosting\":[],\"customerReviews\":[{\"text\":\"Lolinator\",\"dateAdded\":\"2015-03-13 21:42:18.0\",\"sourceWebsite\":\"www.lol.com\",\"id\":\"\"},{\"text\":\"Lolinator\",\"dateAdded\":\"2015-03-13 21:42:38.0\",\"sourceWebsite\":\"www.lol.com\",\"id\":\"\"}],\"expertReviews\":[{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"}]}")
      Ok(views.html.DBTest(k.toString))

      request.
*/
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