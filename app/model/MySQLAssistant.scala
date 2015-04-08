package model

import model.APPModel
import play.api.db._
import play.api.Application
import java.sql.Statement
/**
 * Created by kkk on 3/12/2015.
 */
case class MySQLAssistant(app : Application) extends DBAssistant{

  val db = DB.getConnection()(app)


  /*********************************** GENERAL UTILITY**************************************/

  def createInsertQuery(TN : String, fields: List[String], values: List[String]) : String =
  {

    val fieldsStr = fields.map(x => "`" + x + "`").mkString(",")
    val valuesStr = values.map(x => "'" + x + "'").mkString(",")
    "INSERT INTO `" + TN + "` (" + fieldsStr + ") VALUES (" + valuesStr + ")"

  }

  def lookup(TN : String, colA : String, colB : String, valB: String) : List[String] =
  {
    val stmt = db.createStatement
    val rs = stmt.executeQuery("Select `" + colA + "` from `" + TN + "` where `" + colB + "` = '" + valB + "'")

    var results : List[String] = List()
    while(rs.next())
      results = results :+ rs.getString(colA)
    results
  }


  def insertQuery(TN : String, fields: List[String], values: List[String], returnGeneratedKey : Boolean = false )
   = {
    val stmt = db.createStatement
    stmt.executeUpdate(createInsertQuery(TN, fields, values), Statement.RETURN_GENERATED_KEYS)
    if (returnGeneratedKey){
      val rs = stmt.getGeneratedKeys()
      rs.next()
      rs.getInt(1).toString()
    }
    else ""
  }


  def productExists(productCodes: Map[String, String]) : String =
  {
    val stmt = db.createStatement
    val query = productCodes.map{case (key,value) => value.split(",").map(X => key + " like '%" + X + "%'")}.flatten.mkString(" or ")
    val rs = stmt.executeQuery("select id from `product-codes` where " + query)

    if (rs.next())
      rs.getString("id")
    else
      ""
  }

  /***********************************END GENERAL UTILITY*****************************/





  /**********************************DATA INSERTION***********************************/

  def insertCustomerReview(review : DataCollectionModel.CustomerReview)
  {
    review.text = review.text.replace("'", "")
    val id = productExists(review.codes)
 
    if (id == "") return
    val fields = List("review-text", "source", "product-codes", "title")
    val values = List(review.text, review.websiteName, id, review.title)
    val TN = "customer-review"
    insertQuery(TN, fields, values)

  }


  def insertExpertReview(review : DataCollectionModel.ExpertReview)
  {
    val id = productExists(review.codes)

    if (id == "") return
    val fields = List("url", "title", "website-name", "product-codes")
    val values = List(review.reviewURL, review.title, review.websiteName, id)
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

  def insertWebSellerImage(img : DataCollectionModel.ProductImage): Unit =
  {
    val ids = lookup("web-based-seller", "id", "url", img.sellerURL)
    insertQuery("images", List("URL", "seller-id", "product-codes-id"), List(img.URL, ids(0),productExists(img.codes) ))
  }

  def inserWebSellerDesc(desc : DataCollectionModel.Desc)
  {
    val ids = lookup("web-based-seller", "id", "url", desc.sellerURL)
    insertQuery("desc", List("seller-id", "product-codes", "text"), List(ids(0), productExists(desc.codes), desc.descText))
  }

  def insertWebPosting(posting : DataCollectionModel.WebPosting)
  {
    val ids = lookup("web-based-seller", "id", "url", posting.sellerURL)
    insertQuery("seller-product", List("price", "product-codes", "seller_id", "url"), List(posting.price, productExists(posting.codes), ids(0), posting.postingURL))
  }

  def insertWebSeller(seller : DataCollectionModel.WebSeller) : String =
  {
    val ids = lookup("web-based-seller", "id", "url", seller.URL)

    if (ids.length > 0) return ids(0)
    //inserting seller and retrieving key
    val key = insertSeller(seller.name)

    //inserting web seller
    val fields = List("id", "logo", "url")
    val values = List(key, seller.logo, seller.URL)
    val TN = "web-based-seller"
    insertQuery(TN, fields, values)

    key

  }

  def insertOffer(offer : DataCollectionModel.Offer)
  {
    val sellerID = lookup("web-based-seller", "id", "url", offer.sellerURL)(0)
    val fields = List("price", "description", "start_date", "end_date", "seller_id", "view-count")
    val values = List(offer.price,offer.desc,offer.startDate,offer.endDate,sellerID,offer.viewCount)

    val offerID = insertQuery("offer", fields, values,true)

    offer.codes.foreach(x => insertQuery("offer_products",List("offer_id", "product-codes"), List(offerID, productExists(x))))
  }

  def insertProduct(product : DataCollectionModel.Product)
  {
    val categories = lookup("product-category", "id", "name", product.categoryName)
    var categoryID = ""
    if (categories.length == 0)
      categoryID = insertQuery("product-category", List("name"), List(product.categoryName), true)
    else categoryID = categories(0)

    var codesID = productExists(product.codes)

    if (codesID == "")
    {
      codesID = insertQuery("product-codes", product.codes.keySet.toList, product.codes.values.toList, true)
      insertQuery("product", List("codes", "name", "category-id"), List(codesID, product.name, categoryID))
    }
    else
    {
      val stmt = db.createStatement
      val fields = List("UPC","EAN","NPN","ISBN","ASIN")
      val rs = stmt.executeQuery("select UPC,EAN,NPN,ISBN,ASIN from `product-codes` where id = '" + codesID + "'")
      rs.next()
      val X = Option(rs.getString("EAN")).getOrElse("")
      //
      //


      val codes =
        fields.map(X => (X ,{

            val concatCodes =  Option(rs.getString(X)).getOrElse("").split(",") ++ product.codes.get(X).getOrElse("").split(",")
            concatCodes.distinct.filter(_ != "").mkString(",")

        })).toMap
      stmt.executeUpdate("update `product-codes` set " + codes.map{case(k,v) => k + "='" + v + "'"}.mkString(",") + " where id = '" + codesID + "'")


    }

  }


  def insertRelated(related : DataCollectionModel.Related)
  {
    insertQuery("related", List("PID1", "PID2"), List(productExists(related.C1), productExists(related.C2)))
  }
  def insertSeller(name: String) : String =
  {
    insertQuery("seller", List("name"), List(name), true)
  }

  def insertQuestion(question : DataCollectionModel.Question): Unit =
  {
    val codesID = productExists(question.productCodes)
    val questionID = insertQuery("question", List("question-text", "product-codes"), List(question.question, codesID), true)
    question.answers.foreach(A => insertQuery("answer", List("answer-text", "question-id"), List(A,questionID)))
  }



  /**********************************END DATA INSERTION***********************************/




  /***************************Retrieval Functionality************************************/

  def retrieveProductcodes(codesID : String) : Map[String, String] =
  {
    val stmt = db.createStatement
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select "+ fields.mkString(",") +" from `product-codes` where id ='" + codesID + "'")
    rs.next()

    fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap
  }

  def searchProducts(name : String): List[APPModel.Product] =
  {
    val stmt = db.createStatement
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select name, " + fields.mkString(",") + " from product,`product-codes` where name like '%" + name + "%' and codes = id")
    var results = List[APPModel.Product]()

    while(rs.next()) {
      val value: String = rs.getString("name")
      val key = fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap

      results = results :+ APPModel.Product(APPModel.ProductInfo(key, value, APPModel.Category("", "", ""),"", List("")),  List(""),List(),List() ,List(),List(), List())
    }

    results
  }


  def retrieveProduct(codes :  Map[String, String]): APPModel.Product =
  {
    val stmt = db.createStatement
    val id = productExists(codes)

    var rs = stmt.executeQuery("Select product.name,date_added, c.name from product,`product-category` c where product.codes = '" + id + "' and `category-id` = id")

    rs.next()

    val name = rs.getString("product.name")
    val category = rs.getString("c.name")
    val date = rs.getString("date_added")

    val images = lookup("images", "URL", "product-codes-id", id)
    val desc = lookup("desc","text","product-codes", id)

    var postings : List[APPModel.WebPosting] = List()
    rs = stmt.executeQuery("select price, logo, w.URL, s.url from `seller-product` s, `web-based-seller` w where `product-codes` ='" + id +  "'and seller_id = w.id")
    while(rs.next())
      postings = postings :+ APPModel.WebPosting(rs.getString("price").toDouble,APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.URL")),rs.getString("s.url"))
    //TO DO: DO THE SAME FOR LOCAL SELLERS

    var customerReviews : List[APPModel.CustomerReview] = List()

    rs = stmt.executeQuery("select `review-text`, `date-added`, source from `customer-review` where `product-codes` = '" + id + "'")
    while(rs.next())
      customerReviews = customerReviews :+ APPModel.CustomerReview("",rs.getString("review-text"),rs.getString("date-added"),rs.getString("source"))


    var expertReviews : List[APPModel.ExpertReview] = List()
    rs = stmt.executeQuery("select url, title, `website-name` from `expert-review` where `product-codes` = '" + id + "'")
    while(rs.next())
      expertReviews = expertReviews :+ APPModel.ExpertReview(rs.getString("url"),rs.getString("title"),rs.getString("website-name"))

    APPModel.Product(APPModel.ProductInfo(codes,name,APPModel.Category("","",category), date, images),desc,postings,List(),customerReviews,expertReviews, List())

  }



}
