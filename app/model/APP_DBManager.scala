package model

/**
 * Created by kkk on 3/8/2015.
 */





object APP_DBManager {

  import play.api.Play.current
  import APPModel._
  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]
  def test: String =
  {
    //insertExpertReview("Lolinator", "www.lol.com", Map("UPC" -> "2555"), "why iphones suck")
    //insertCustomerReview("Lolinator", "www.lol.com", Map("UPC" -> "2555"))
    // insertWebSellerProduct(Map("UPC" -> "25555"),"iphone", "Really fucking shiny22", "iphone2232.jpg", "phones", "50000", "www.amazon.com/iphoneLOL", "Amazon", "logo2.jpg", "www.amazon.com")

    //searchProducts("iph").toString()
    retrieveProduct(Map("UPC" -> "2555")).toString
  }


  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {

    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }



  def searchProducts(name : String): List[APPModel.Product]  =
  {
    mySqlAssistant.searchProducts(name)
  }

  def retrieveProduct(codes :  Map[String, String]): APPModel.Product =
  {
    mySqlAssistant.retrieveProduct(codes)
  }

  def retrieveAllCategories() : List[APPModel.Category] =
  {
    mySqlAssistant.retrieveAllCategories()
  }

  def retrieveOffersInCategory(name : String) : List[APPModel.WebOffer] =
  {
    mySqlAssistant.retrieveOffersInCategory(name)
  }








}
