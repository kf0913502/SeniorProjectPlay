package model

/**
 * Created by kkk on 3/8/2015.
 */





object DBManager {

  import play.api.Play.current

  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]
  def test: String =
  {
    //insertExpertReview("Lolinator", "www.lol.com", "12334", "why iphones suck")
    //insertCustomerReview("Lolinator", "www.lol.com", "12334")
    searchProducts("KP%").toString()

  }

  def insertCustomerReview(reviewText: String, reviewSource: String, UPC: String) =
  {

    mySqlAssistant.insertCustomerReview(reviewText, reviewSource, UPC)

  }

  def insertExpertReview(websiteName: String, URL: String, UPC: String, title: String)
  {
    mySqlAssistant.insertExpertReview(websiteName, URL, UPC, title)

  }

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {

    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }

  def insertWebSeller(name: String, logo: String, URL: String)
  {

    mySqlAssistant.insertWebSeller(name, logo, URL)

  }

  def insertWebSellerProduct(UPC : String, price: Double, URL: String, name: String, desc: String, img: String, category : String)
  {
    mySqlAssistant.insertWebSellerProduct(UPC, price, URL, name, desc, img, category)
  }

  def insertCategory(name : String, parent : String = "")
  {
    mySqlAssistant.insertCategory(name, parent)
  }

  def searchProducts(name : String): Map[String, String] =
  {
    mySqlAssistant.searchProducts(name)
  }
}
