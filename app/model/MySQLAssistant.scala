package model

import play.api.db._
import play.api.Application
import java.sql.Statement
/**
 * Created by kkk on 3/12/2015.
 */
class MySQLAssistant(app : Application) extends DBAssistant{

  val db = DB.getConnection()(app)
  val stmt = db.createStatement


  def createInsertQuery(TN : String, fields: List[String], values: List[String]) : String =
  {

    val fieldsStr = fields.map(x => "`" + x + "`").mkString(",")
    val valuesStr = values.map(x => "'" + x + "'").mkString(",")
    "INSERT INTO `" + TN + "` (" + fieldsStr + ") VALUES (" + valuesStr + ")"

  }
  def insertQuery(TN : String, fields: List[String], values: List[String], returnGeneratedKey : Boolean = false )
   = {

    stmt.executeUpdate(createInsertQuery(TN, fields, values), Statement.RETURN_GENERATED_KEYS)
    if (returnGeneratedKey){
      val rs = stmt.getGeneratedKeys()
      rs.next()
      rs.getInt(1).toString()
    }
    else ""
  }

  def insertCustomerReview(reviewText: String, reviewSource: String, UPC: String) =
  {
    val fields = List("review-text", "source", "upc")
    val values = List(reviewText, reviewSource, UPC)
    val TN = "customer-review"
    insertQuery(TN, fields, values)

  }

  def insertExpertReview(websiteName: String, URL: String, UPC: String, title: String)
  {
    val fields = List("url", "title", "website-name", "upc")
    val values = List(URL, title, websiteName, UPC)
    val TN = "expert-review"
    insertQuery(TN, fields, values)

  }

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {
    val fields = List("username", "password", "firstname", "lastname", "email", "active")
    val values = List(userName, password, firstName, lastName, email, "0")
    val TN = "user-account"
    insertQuery(TN, fields, values)

  }

  def insertSeller(name: String) : String =
  {
    insertQuery("seller", List("name"), List(name), true)
  }
  def insertWebSeller(name: String, logo: String, URL: String)
  {
    //inserting seller and retrieving key
    val key = insertSeller(name)

    //inserting web seller
    val fields = List("id", "logo", "url")
    val values = List(key, logo, URL)
    val TN = "web-based-seller"
    insertQuery(TN, fields, values)

  }

  def insertWebSellerProduct(UPC : String, Price: Double, URL: String)
  {
    var rs = stmt.executeQuery("Select id from web-based-seller where url = '" + URL + "'")
    rs.next()
    val id = rs.getString("id")
    val fields = List("price", "upc", "seller_id")
    val values = List(Price.toString(), UPC, id)
    val TN = "seller-product"
    insertQuery(TN, fields, values)

  }

}
