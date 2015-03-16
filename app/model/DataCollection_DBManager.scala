package model

/**
 * Created by kkk on 3/8/2015.
 */





object DataCollection_DBManager {

  import play.api.Play.current
  import DataCollectionModel._
  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {

    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }


  def insertWebSellerProduct(): Unit =
  {
    mySqlAssistant.insertWebSellerProduct(productCodes, name, desc, img, category, price.toString,listingURL , sellerName ,logo, URL)
  }


  def insertWebSellerImage(img : ProductImage): Unit =
  {
    mySqlAssistant.insertWebSellerImage(img)
  }

  def insertWebSellerDesc(codes : Map[String, String], URL: String, desc : String): Unit =
  {
    //insertWebSellerProduct(co)
  }

  def insertWebSellerProductInfo(codes : Map[String, String], URL: String, info : ProductInfo): Unit =
  {

  }

  def insertWebSellerPosting(codes : Map[String, String], URL: String,posting : WebPosting): Unit =
  {

  }

  def insertCustomerReview(reviewText: String, reviewSource: String, productCodes: Map[String, String]) =
  {

    mySqlAssistant.insertCustomerReview(reviewText, reviewSource, productCodes)

  }
  def insertExpertReview(websiteName: String, URL: String, productCodes: Map[String, String], title: String)
  {
    mySqlAssistant.insertExpertReview(websiteName, URL, productCodes, title)

  }


}
