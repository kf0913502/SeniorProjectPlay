package model

import play.api.db._
import play.api.Application
import java.sql.Statement
/**
 * Created by kkk on 3/12/2015.
 */
case class MySQLAssistant(app : Application) extends DBAssistant{

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

  def insertCustomerReview(reviewText: String, reviewSource: String, productCodes: Map[String, String])
  {
    val id = productExists(productCodes)

    if (id == "") return
    val fields = List("review-text", "source", "product-codes")
    val values = List(reviewText, reviewSource, id)
    val TN = "customer-review"
    insertQuery(TN, fields, values)

  }

  def productExists(productCodes: Map[String, String]) : String =
  {
    val query = productCodes.map{case (key,value) => key + " = '" + value + "'"}.mkString(" or ")
    val rs = stmt.executeQuery("select id from `product-codes` where " + query)

    if (rs.next())
      rs.getString("id")
    else
      ""
  }
  def insertExpertReview(websiteName: String, URL: String, productCodes: Map[String, String], title: String)
  {
    val id = productExists(productCodes)

    if (id == "") return
    val fields = List("url", "title", "website-name", "product-codes")
    val values = List(URL, title, websiteName, id)
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
  def insertWebSeller(name: String, logo: String, URL: String) : String =
  {
    val ids = lookup("web-based-seller", "id", "url", URL)

    if (ids.length > 0) return ids(0)
    //inserting seller and retrieving key
    val key = insertSeller(name)

    //inserting web seller
    val fields = List("id", "logo", "url")
    val values = List(key, logo, URL)
    val TN = "web-based-seller"
    insertQuery(TN, fields, values)

    key

  }

  def lookup(TN : String, colA : String, colB : String, valB: String) : List[String] =
  {
    val rs = stmt.executeQuery("Select `" + colA + "` from `" + TN + "` where `" + colB + "` = '" + valB + "'")

    var results : List[String] = List()
    while(rs.next())
      results = results :+ rs.getString(colA)
    results
  }


  def insertWebSellerProduct(productCodes : Map[String, String], name: String, desc: String, img: String, category : String, price : String, listingURL : String, sellerName: String, logo: String, URL: String): Unit =
  {
    val sellerId = insertWebSeller(sellerName, logo, URL)
    insertProduct(productCodes, name, desc, img, category, price, sellerId, listingURL)
  }

  def insertProduct(productCodes : Map[String, String], name: String, desc: String, img: String, category : String, price : String, sellerID : String, listingURL : String)
  {
    val categories = lookup("product-category", "id", "name", category)
    var categoryID = ""
    if (categories.length == 0)
      categoryID = insertQuery("product-category", List("name"), List(category), true)
    else categoryID = categories(0)

    var codesID = productExists(productCodes)

    if (codesID == "")
    {
      codesID = insertQuery("product-codes", productCodes.keySet.toList, productCodes.values.toList, true)
      insertQuery("product", List("codes", "name", "category-id"), List(codesID, name, categoryID))
    }

    if (img != "")
    insertQuery("images", List("URL", "seller-id", "product-codes-id"), List(img, sellerID,codesID ))

    if (desc != "")
    insertQuery("desc", List("seller-id", "product-codes", "text"), List(sellerID, codesID, desc))

    if (price != "")
    insertQuery("seller-product", List("price", "product-codes", "seller_id", "url"), List(price, codesID, sellerID, listingURL))
  }

  def insertWebSellerProductInfo(sellerName : String, logo : String, URL : String, category : Category): Unit =
  {
    /*val sellerId = insertWebSeller(sellerName, logo, URL)
    val categories = lookup("product-category", "id", "name", category)
    var categoryID = ""
    if (categories.length == 0)
      categoryID = insertQuery("product-category", List("name"), List(category), true)
    else categoryID = categories(0)

    var codesID = productExists(productCodes)

    if (codesID == "")
    {
      codesID = insertQuery("product-codes", productCodes.keySet.toList, productCodes.values.toList, true)
      insertQuery("product", List("codes", "name", "category-id"), List(codesID, name, categoryID))
    }

    */


  }

  def retrieveProductcodes(codesID : String) : Map[String, String] =
  {
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select "+ fields.mkString(",") +" from `product-codes` where id ='" + codesID + "'")
    rs.next()

    fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap
  }

  def searchProducts(name : String): List[Product] =
  {
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select name, " + fields.mkString(",") + " from product,`product-codes` where name like '" + name + "%' and codes = id")
    var results = List[Product]()

    while(rs.next()) {
      val value: String = rs.getString("name")
      val key = fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap

      //results = results :+ Product(key, name, "",Category("", "", ""), List(""), List(""),WebPosting(0, "") )
    }

    results
  }


  def retrieveProduct(codes :  Map[String, String]): Product =
  {
    val id = productExists(codes)

    var rs = stmt.executeQuery("Select product.name,date_added, c.name from product,`product-category` c where product.codes = '" + id + "' and `category-id` = id")

    rs.next()

    val name = rs.getString("product.name")
    val category = rs.getString("c.name")
    val date = rs.getString("date_added")

    val images = lookup("images", "URL", "product-codes-id", id)
    val desc = lookup("desc","text","product-codes", id)

    var postings : List[WebPosting] = List()
    rs = stmt.executeQuery("select price, logo, w.URL, s.url from `seller-product` s, `web-based-seller` w where `product-codes` ='" + id +  "'and seller_id = w.id")
    while(rs.next())
      postings = postings :+ WebPosting(rs.getString("price").toDouble,WebBasedSeller(rs.getString("logo"),rs.getString("w.URL")),rs.getString("s.url"))
    //TO DO: DO THE SAME FOR LOCAL SELLERS

    var customerReviews : List[CustomerReview] = List()

    rs = stmt.executeQuery("select `review-text`, `date-added`, source from `customer-review` where `product-codes` = '" + id + "'")
    while(rs.next())
      customerReviews = customerReviews :+ CustomerReview(rs.getString("review-text"),rs.getString("date-added"),rs.getString("source"))


    var expertReviews : List[ExpertReview] = List()
    rs = stmt.executeQuery("select url, title, `website-name` from `expert-review` where `product-codes` = '" + id + "'")
    while(rs.next())
      expertReviews = expertReviews :+ ExpertReview(rs.getString("url"),rs.getString("title"),rs.getString("website-name"))

    Product(ProductInfo(codes,name,Category("","",category), date),images,desc,postings,List(),customerReviews,expertReviews)

  }



}
