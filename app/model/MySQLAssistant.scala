package model


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
    var query = "Select `" + colA + "` from `" + TN + "` "
    if (colB != "") query = query + "where `" + colB + "` = '" + valB + "'"
    val rs = stmt.executeQuery(query)

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
    val codes = productCodes.filter(X => X._2 != "" && X._2 != null)

    val query = codes.map{case (key,value) => value.split(",").map(X => "(" + key + " like '%," + X + ",%' or " + key + " like '" + X + ",%' or " + key + " like '%," + X + "' or " + key + " like '" + X + "')")}.flatten.mkString(" or ")
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

  def insertOffer(offer : DataCollectionModel.WebOffer)
  {
    val sellerID = lookup("web-based-seller", "id", "url", offer.sellerURL)(0)
    val fields = List("price", "description", "start_date", "end_date", "seller_id", "view-count")
    val values = List(offer.price,offer.desc,offer.startDate,offer.endDate,sellerID,offer.viewCount)

    val offerID = insertQuery("offer", fields, values,true)

    offer.codes.foreach(x => insertQuery("offer_products",List("offer_id", "product-codes"), List(offerID, productExists(x))))
  }


  def insertProduct(product : DataCollectionModel.Product) {


    val parentcategorie = lookup("product-category", "id", "name", product.parentCategoryName)
    var parentCategoryID = ""
    if (parentcategorie.length == 0)
      parentCategoryID = insertQuery("product-category", List("name"), List(product.parentCategoryName), true)
    else parentCategoryID = parentcategorie(0)

    val categories = lookup("product-category", "id", "name", product.categoryName)
    var categoryID = ""
    if (categories.length == 0)
      categoryID = insertQuery("product-category", List("name", "parent_id"), List(product.categoryName, parentCategoryID), true)
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
      var codes =
        fields.map(X => (X ,{
            val concatCodes =  Option(rs.getString(X)).getOrElse("").split(",") ++ product.codes.get(X).getOrElse("").split(",")
            concatCodes.distinct.filter(_ != "").mkString(",")
        })).toMap

      codes = codes.filter(_._2 != "")
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


  def insertOntologyNodes(ontologyNode : DataCollectionModel.OntologyNode, codesID : String, parentKey : String)
  {
    val key = {
      if (parentKey != "")
      insertQuery("ontology-nodes",List("product-codes", "parent"),List(codesID, parentKey),true)
    else
      insertQuery("ontology-nodes",List("product-codes"),List(codesID),true)
    }
    ontologyNode.features.foreach(f => insertQuery("ontology-features", List("feature", "nodeID"), List(f,key)))

    ontologyNode.children.foreach(insertOntologyNodes(_,codesID,key))

  }


  def insertOntologyTree(ontologyTree: DataCollectionModel.OntologyTree): Unit =
  {
    insertOntologyNodes(ontologyTree.root,ontologyTree.codesID,"")
  }
  def insertWebPriceReduction(reduction: DataCollectionModel.WebPriceReduction): Unit =
  {
    val values = List(productExists(reduction.codes), lookup("web-based-seller", "id", "url", reduction.sellerURL)(0), reduction.oldPrice, reduction.newPrice )
    insertQuery("price-reduction", List("product-codes", "sellerID", "oldPrice", "newPrice"), values)
  }
  /**********************************END DATA INSERTION***********************************/




  /***************************Retrieval Functionality************************************/

  def retrieveAllProductcodesID() : List[String] =
  {
    lookup("product-codes","id","1", "1")

  }
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

      results = results :+ APPModel.Product(APPModel.ProductInfo(key, value, APPModel.Category("", "", ""),"", List("")),  List(""),List(),List() ,List(),List(), List(), List(), List(), List())
    }

    results
  }


  def retrieveProductInfo(codes : Map[String, String]) : APPModel.ProductInfo =
  {

    val stmt = db.createStatement
    val id = productExists(codes)

    val rs = stmt.executeQuery("Select product.name,date_added, c.name from product,`product-category` c where product.codes = '" + id + "' and c.id = `category-id`")
    rs.next()

    val name = rs.getString("product.name")
    val category = rs.getString("c.name")
    val date = rs.getString("date_added")

    val images = lookup("images", "URL", "product-codes-id", id)

    APPModel.ProductInfo(codes, name, APPModel.Category("","",category),date,images)
  }

  def retrieveWebPostings(codesID : String) : List[APPModel.WebPosting] =
  {
    val stmt = db.createStatement
    var postings : List[APPModel.WebPosting] = List()
    val rs = stmt.executeQuery("select price, logo, w.URL, s.url from `seller-product` s, `web-based-seller` w where `product-codes` ='" + codesID +  "'and seller_id = w.id")
    while(rs.next())
      postings = postings :+ APPModel.WebPosting(rs.getString("price").toDouble,APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.URL")),rs.getString("s.url"))
    postings
  }

  def retrieveCustomerReviews(codesID : String) : List[APPModel.CustomerReview] =
  {
    val stmt = db.createStatement
    var customerReviews : List[APPModel.CustomerReview] = List()

    val rs = stmt.executeQuery("select `review-text`, `date-added`, source from `customer-review` where `product-codes` = '" + codesID + "'")
    while(rs.next())
      customerReviews = customerReviews :+ APPModel.CustomerReview("",rs.getString("review-text"),rs.getString("date-added"),rs.getString("source"))
    customerReviews
  }

  def retrieveExpertReviews(codesID : String) : List[APPModel.ExpertReview] =
  {
    val stmt = db.createStatement
    var expertReviews : List[APPModel.ExpertReview] = List()
    val rs = stmt.executeQuery("select url, title, `website-name` from `expert-review` where `product-codes` = '" + codesID + "'")
    while(rs.next())
      expertReviews = expertReviews :+ APPModel.ExpertReview(rs.getString("url"),rs.getString("title"),rs.getString("website-name"))
    expertReviews
  }

  def retrieveRelatedProducts(codesID : String) : List[APPModel.ProductInfo] =
   {
     val ids = lookup("related", "PID2", "PID1", codesID)
     var relatedProducts : List[APPModel.ProductInfo] = List()

     ids.foreach(X => {
       val codes = retrieveProductcodes(X)
       val productInfo = retrieveProductInfo(codes)
       relatedProducts = relatedProducts :+ productInfo
     })

    relatedProducts
   }

  def retrieveQuestions(codesID : String) : List[APPModel.Question] =
  {
    val stmt = db.createStatement
    var questions : List[APPModel.Question] = List()
    val rs = stmt.executeQuery("select id, `question-text` from question where `product-codes` = '" + codesID + "'")
    while(rs.next())
    {
      val answers = lookup("answer", "answer-text", "question-id", rs.getString("id"))
      questions = questions :+ APPModel.Question(rs.getString("question-text"),answers)
    }
    questions
  }

  def retrievePriceReductions(codesID : String) : List[APPModel.PriceReduction] =
  {
    val stmt = db.createStatement
    var reductions : List[APPModel.PriceReduction] = List()
    val rs = stmt.executeQuery("select oldPrice,newPrice, logo, w.url from `price-reduction` p, `web-based-seller` w where `product-codes` ='" + codesID +  "'and sellerID = w.id")

    val productInfo = retrieveProductInfo(retrieveProductcodes(codesID))
    while(rs.next())
      reductions = reductions :+ APPModel.PriceReduction(APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.url")),productInfo,rs.getString("newPrice"),rs.getString("oldPrice"))
    reductions
  }

  def retrieveOffers(codesID : String) : List[APPModel.WebOffer] =
  {
    val stmt = db.createStatement
    var offers : List[APPModel.WebOffer] = List()
    val offersID = lookup("offer_products","offer_id","product-codes",codesID)

    offersID.foreach(X =>{
      val productsID = lookup("offer_products","product-codes","offer_id",X)
      val query = "select price,description,start_date,end_date, logo,w.url from " +
        "offer o, `web-based-seller` w where o.id ='" + X + "' and w.id = o.seller_id"
      val rs = stmt.executeQuery(query)
      rs.next()
      val productInfos = productsID.map(X => retrieveProductInfo(retrieveProductcodes(X)))

      offers = offers :+ APPModel.WebOffer(productInfos,rs.getString("description"),rs.getString("price"),
        APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.url")),rs.getString("start_date"),rs.getString("end_date"))
    })

    offers
  }
  def retrieveProduct(codes :  Map[String, String]): APPModel.Product =
  {
    val id = productExists(codes)
    if (id == "") return null
    val productInfo = retrieveProductInfo(codes)
    val desc = lookup("desc","text","product-codes", id)
    val postings = retrieveWebPostings(id)
    val customerReviews = retrieveCustomerReviews(id)
    val expertReviews = retrieveExpertReviews(id)
    val relatedProducts = retrieveRelatedProducts(id)
    val questions = retrieveQuestions(id)
    val priceReductions = retrievePriceReductions(id)
    val offers = retrieveOffers(id)
       //TO DO: DO THE SAME FOR LOCAL SELLERS


    APPModel.Product(productInfo,desc,postings,List(),customerReviews,expertReviews, offers, relatedProducts, questions, priceReductions)

  }

  def retrieveAllCategories() : List[APPModel.Category] =
  {

      lookup("product-category","name","","").map(X => APPModel.Category("","",X))
    //TODO: 1. see how to display images for categories 2. see how to make use of parent category
  }

  def retrieveOffersInCategory(categoryName : String) : List[APPModel.WebOffer] =
  {
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select logo, w.url, off.id, price, description, start_date, end_date " +
      "from product pr, offer_products op,`product-category` pc, offer off ,`web-based-seller` w where " +
      "w.id = off.seller_id and op.offer_id = off.id and `category-id` = pc.id and pr.codes = `product-codes` and pc.name = '" + categoryName + "' ORDER BY off.date_added")

    var offers : List[APPModel.WebOffer] = List()
    while(rs.next())
    {
      val productInfos = lookup("offer_products","product-codes", "offer_id",rs.getString("off.id")).map(X => retrieveProductInfo(retrieveProductcodes(X)))
      offers = offers :+ APPModel.WebOffer(productInfos,rs.getString("description"),rs.getString("price"),
      APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.url")),rs.getString("start_date"),rs.getString("end_date"))
    }

    offers

  }

  def retrieveOntologyTree(codesID : String): DataCollectionModel.OntologyTree =
  {
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select * from `ontology-nodes` where `product-codes` = '" + codesID + "' ORDER BY parent" )

    val nodes  = scala.collection.mutable.Map[String, DataCollectionModel.OntologyNode]()
    var root = true
    var rootNode : DataCollectionModel.OntologyNode = null
    while(rs.next())
    {

      nodes(rs.getString("id")) = DataCollectionModel.OntologyNode(List(),List(),0)

      if (root)
        rootNode = nodes(rs.getString("id"))

          root = false

      if (rs.getString("parent") != null)
      nodes(rs.getString("parent")).children = nodes(rs.getString("parent")).children :+ nodes(rs.getString("id"))

      val features = lookup("ontology-features","feature", "nodeID",rs.getString("id"))

      nodes(rs.getString("id")).features = features
    }


    DataCollectionModel.OntologyTree(rootNode,codesID)



  }



}
