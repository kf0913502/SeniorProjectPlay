package model

/**
 * Created by kkk on 3/8/2015.
 */





object DBManager {

  import play.api.Play.current

  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]
  def test: String =
  {
    //insertExpertReview("Lolinator", "www.lol.com", Map("UPC" -> "2555"), "why iphones suck")
    //insertCustomerReview("Lolinator", "www.lol.com", Map("UPC" -> "2555"))
   // insertWebSellerProduct(Map("UPC" -> "25555"),"iphone", "Really fucking shiny22", "iphone2232.jpg", "phones", "50000", "www.amazon.com/iphoneLOL", "Amazon", "logo2.jpg", "www.amazon.com")

    //searchProducts("iph").toString()
    retrieveProduct(Map("UPC" -> "2555")).toString
  }

  def insertCustomerReview(reviewText: String, reviewSource: String, productCodes: Map[String, String]) =
  {

    mySqlAssistant.insertCustomerReview(reviewText, reviewSource, productCodes)

  }
  def insertExpertReview(websiteName: String, URL: String, productCodes: Map[String, String], title: String)
  {
    mySqlAssistant.insertExpertReview(websiteName, URL, productCodes, title)

  }

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {

    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }


  def insertWebSellerProduct(productCodes : Map[String, String], name: String, desc: String, img: String, category : String, price : String, listingURL : String, sellerName: String, logo: String, URL: String): Unit =
  {
    mySqlAssistant.insertWebSellerProduct(productCodes, name, desc, img, category, price.toString,listingURL , sellerName ,logo, URL)
  }

  def searchProducts(name : String): List[Product]  =
  {
    mySqlAssistant.searchProducts(name)
  }

  def retrieveProduct(codes :  Map[String, String]): Product =
  {
    mySqlAssistant.retrieveProduct(codes)
  }
}
