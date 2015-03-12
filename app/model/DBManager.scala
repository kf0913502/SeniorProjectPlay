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
    insertWebSeller("Kariem", "lol", "zors")
    "DONE"
  }

  def insertCustomerReview(reviewText: String, reviewSource: String, UPC: String) =
  {
    val fields = List("review-text", "source", "upc")
    val values = List(reviewText, reviewSource, UPC)
    val TN = "customer-review"
    mySqlAssistant.insertQuery(TN, fields, values)

  }

  def insertExpertReview(websiteName: String, URL: String, UPC: String, title: String)
  {
    val fields = List("url", "title", "website-name", "upc")
    val values = List(URL, title, websiteName, UPC)
    val TN = "expert-review"
    mySqlAssistant.insertQuery(TN, fields, values)

  }

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {
    val fields = List("username", "password", "firstname", "lastname", "email", "active")
    val values = List(userName, password, firstName, lastName, email, "0")
    val TN = "user-account"
    mySqlAssistant.insertQuery(TN, fields, values)

  }

  def insertSeller(name: String) : String =
  {
      mySqlAssistant.insertQuery("seller", List("name"), List(name), true)
  }
  def insertWebSeller(name: String, logo: String, URL: String)
  {
    //inserting seller and retrieving key
    val key = insertSeller(name)

    //inserting web seller
    val fields = List("id", "logo", "url")
    val values = List(key, logo, URL)
    val TN = "web-based-seller"
    mySqlAssistant.insertQuery(TN, fields, values)

  }

  def insertWebSellerProduct(UPC : String, Price: Double)
  {

  }

}
