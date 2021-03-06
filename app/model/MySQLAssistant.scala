package model


import java.io.{ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream}

import edu.stanford.nlp.util.CoreMap
import SentimentAnalysis.{SentimentCalculator, WeightedGraph}
import model.APPModel.PriceReduction
import play.api.db._
import play.api.Application
import java.sql.Statement
import scala.collection.mutable.Queue
import play.api.libs.json.{Json, JsError}
case class MySQLAssistant(app : Application) extends DBAssistant{


  implicit def bool2int(b : Boolean) = if (b) 1 else 0

  /*********************************** GENERAL UTILITY**************************************/

  def createInsertQuery(TN : String, fields: List[String], values: List[String]) : String =
  {

    val fieldsStr = fields.map(x => "`" + x + "`").mkString(",")
    val valuesStr = values.map(x => "'" + x + "'").mkString(",")
    "INSERT INTO `" + TN + "` (" + fieldsStr + ") VALUES (" + valuesStr + ")"

  }

  def lookup(TN : String, colA : String, colB : String, valB: String) : List[String] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var query = "Select `" + colA + "` from `" + TN + "` "
    if (colB != "") query = query + "where `" + colB + "` = '" + valB + "'"
    val rs = stmt.executeQuery(query)

    var results : List[String] = List()
    while(rs.next())
      results = results :+ rs.getString(colA)
    db.close()
    stmt.close()
    results

  }


  def insertQuery(TN : String, fields: List[String], values: List[String], returnGeneratedKey : Boolean = false )
   = {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    stmt.executeUpdate(createInsertQuery(TN, fields, values), Statement.RETURN_GENERATED_KEYS)
    db.close()

    if (returnGeneratedKey){
      val rs = stmt.getGeneratedKeys()

      rs.next()

      val result = rs.getInt(1).toString()
      stmt.close()
      db.close()
      result
    }
    else
    {
      stmt.close()
      db.close()
      ""
    }

  }


  def productExists(productCodes: Map[String, String]) : String =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val codes = productCodes.filter(X => X._2 != "" && X._2 != null)

    val query = codes.map{case (key,value) => value.split(",").map(X => "(" + key + " like '%," + X + ",%' or " + key + " like '" + X + ",%' or " + key + " like '%," + X + "' or " + key + " like '" + X + "')")}.flatten.mkString(" or ")
    val rs = stmt.executeQuery("select id from `product-codes` where " + query)

    if (rs.next())
    {

      val result = rs.getString("id")
      stmt.close()
      db.close()

      result
    }
    else
    {
      stmt.close()
      db.close()

      ""
    }
  }

  /***********************************END GENERAL UTILITY*****************************/



  def generateReviewSentences(): Unit =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement()

    val allProducts = lookup("product-codes","id","","")

    allProducts.foreach(x => {
      retrieveCustomerReviews(x).foreach(y => {
        var sentences = SentimentCalculator.getSentences(y)
        var graphs = SentimentCalculator.getGraphs(sentences)

        /*Should be in class sentence;*/
        var sentenceOutputStream = new ByteArrayOutputStream()
        var sentenceOOutputStream = new ObjectOutputStream(sentenceOutputStream)
        sentenceOOutputStream.writeObject(sentences)
        var sentencesBytes = sentenceOutputStream.toByteArray()
        /*Should be in class sentence*/

        /*Should be in class List[Graphs];*/
        var graphsOutputStream = new ByteArrayOutputStream()
        var graphsOOutputStream = new ObjectOutputStream(graphsOutputStream)
        graphsOOutputStream.writeObject(graphs.map(x => (x.nodes, x.edges)))
        var graphsBytes = graphsOutputStream.toByteArray()
        /*Should be in class List[Graphs]*/


        var graphsInputStream = new ByteArrayInputStream(graphsBytes)
        var sentencesInputStream = new ByteArrayInputStream(sentencesBytes)

        val db = DB.getConnection()(app)
        val pstmt = db.prepareStatement("INSERT INTO `review-sentences` VALUES(?,?," + y.id + ", " + x + ")")
        pstmt.setBinaryStream(1, sentencesInputStream, sentencesBytes.length)
        pstmt.setBinaryStream(2, graphsInputStream, graphsBytes.length)
        pstmt.executeUpdate()
        pstmt.close()


        sentences = null
        graphs = null
        graphsInputStream = null
        sentencesInputStream = null

        graphsOutputStream = null
        graphsOOutputStream = null
        graphsBytes = null



        sentencesBytes = null
        sentenceOOutputStream = null
        sentenceOutputStream = null
    })})

    db.close()
  }

  /**********************************DATA INSERTION***********************************/

  def insertProductSentiment(sentimentTree : DataCollectionModel.OntologyTree, codes : Map[String, String]): Unit =
  {
    val fields = List("product_codes", "sentiment_pos", "feature")

    sentimentTree.getBFSNodes().foreach(x => insertQuery("sentiment",fields,List(productExists(codes), x.sentiment.toString,x.features(0)) ))
  }
  def insertCustomerReview(review : DataCollectionModel.CustomerReview)
  {

    review.text = review.text.replace("'", "")
//    println(review.text)
    val id = productExists(review.codes)
 
    if (id == "") return
    val fields = List("review-text", "source", "product-codes", "title")
    val values = List(review.text, review.websiteName, id, review.title.replace("'", ""))
    val TN = "customer-review"
    val key = insertQuery(TN, fields, values,true)


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

  def insertUserAccount(user : APPModel.User)
  {
    val fields = List("username", "password", "firstname", "lastname", "email", "active")
    val values = List(user.username, user.password, user.firstname, user.lastname, user.email, "0")
    val TN = "user-account"
    insertQuery(TN, fields, values)

  }

  def insertWebSellerImage(img : DataCollectionModel.ProductImage): Unit =
  {
    try {
      val ids = lookup("web-based-seller", "id", "url", img.sellerURL)
      insertQuery("images", List("URL", "seller-id", "product-codes-id"), List(img.URL, ids(0), productExists(img.codes)))
    }
    catch {
      case e : Exception => println("Duplicate Image")
    }
  }

  def inserWebSellerDesc(desc : DataCollectionModel.Desc)
  {
    val ids = lookup("web-based-seller", "id", "url", desc.sellerURL)
    insertQuery("desc", List("seller-id", "product-codes", "text"), List(ids(0), productExists(desc.codes), desc.descText.replace("'","")))
  }

  def insertWebPosting(posting : DataCollectionModel.WebPosting)
  {
    val ids = lookup("web-based-seller", "id", "url", posting.sellerURL)
    insertQuery("seller-product", List("price", "product-codes", "seller_id", "url", "used"), List(posting.price, productExists(posting.codes), ids(0), posting.postingURL, (posting.used : Int).toString))
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
      val db = DB.getConnection()(app)
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
      stmt.close()
      db.close()


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
    val questionID = insertQuery("question", List("question-text", "product-codes"), List(question.question.replace("'",""), codesID), true)
    question.answers.foreach(A => insertQuery("answer", List("answer-text", "question-id"), List(A.replace("'",""),questionID)))
  }


  def insertOntologyNodes(ontologyNode : DataCollectionModel.OntologyNode, category : String, parentKey : String)
  {
    val categoryID = lookup("product-category","id", "name", category)(0)
    val key = {
      if (parentKey != "")
      insertQuery("ontology-nodes",List("category-id", "parent"),List(categoryID, parentKey),true)
    else
      insertQuery("ontology-nodes",List("category-id"),List(categoryID),true)
    }
    ontologyNode.features.foreach(f => insertQuery("ontology-features", List("feature", "nodeID"), List(f,key)))

    ontologyNode.children.foreach(insertOntologyNodes(_,category,key))

  }


  def insertOntologyTree(ontologyTree: DataCollectionModel.OntologyTree): Unit =
  {
    insertOntologyNodes(ontologyTree.root,ontologyTree.category,"")
  }
  def insertWebPriceReduction(reduction: DataCollectionModel.WebPriceReduction): Unit =
  {
    val values = List(productExists(reduction.codes), lookup("web-based-seller", "id", "url", reduction.sellerURL)(0), reduction.oldPrice, reduction.newPrice )
    insertQuery("price-reduction", List("product-codes", "sellerID", "oldPrice", "newPrice"), values)
  }
  /**********************************END DATA INSERTION***********************************/




  /***************************Retrieval Functionality************************************/

  def retrieveAllProductcodes() : List[Map[String, String]] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")

    lookup("product-codes","id","", "")
    var result = List[Map[String, String]]()
    val rs = stmt.executeQuery("Select " + fields.mkString(",") + " from `product-codes`")
    while(rs.next())
    {
      var codes = Map[String, String]()
      fields.foreach(i => codes += (i -> rs.getString(i)))
      result :+= codes
    }
    stmt.close()
    db.close()

    result
  }


  def retrieveProductcodes(codesID : String) : Map[String, String] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select "+ fields.mkString(",") +" from `product-codes` where id ='" + codesID + "'")
    rs.next()


    val result = fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap
    stmt.close()
    db.close()

    result
  }

  def searchProducts(name : String): List[APPModel.Product] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val fields = List("UPC", "EAN", "NPN", "ISBN", "ASIN")
    val rs = stmt.executeQuery("Select name, id, " + fields.mkString(",") + " from product,`product-codes` where name like '" + name + "%' and codes = id")
    var results = List[APPModel.Product]()

    while(rs.next()) {
      val value: String = rs.getString("name")
      val key = fields.filter(x => rs.getString(x) != "").map(x => (x, rs.getString(x))).toMap
      val images = lookup("images", "URL", "product-codes-id", rs.getString("id"))

      results = results :+ APPModel.Product(APPModel.ProductInfo(key, value, APPModel.Category("", "", ""),"", images),  List(""),List(),List() ,List(),List(), List(), List(), List(), List())
    }
    stmt.close()
    db.close()

    results
  }


  def retrieveProductInfo(codes : Map[String, String]) : APPModel.ProductInfo =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val id = productExists(codes)

    val rs = stmt.executeQuery("Select product.name,date_added,c.id, c.parent_id, c.name from product,`product-category` c where product.codes = '" + id + "' and c.id = `category-id`")
    if (!rs.next()) return null

    val name = rs.getString("product.name")
    val category = rs.getString("c.name")
    val date = rs.getString("date_added")
    val categoryID = rs.getString("c.id")
    val categoryParentID = rs.getString("c.parent_id")
    val images = lookup("images", "URL", "product-codes-id", id)
    stmt.close()
    db.close()
    APPModel.ProductInfo(codes, name, APPModel.Category(categoryID,categoryParentID,category),date,images)
  }

  def retrieveWebPostings(codesID : String) : List[APPModel.WebPosting] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var postings : List[APPModel.WebPosting] = List()
    val rs = stmt.executeQuery("select price, logo, w.URL,used, s.url from `seller-product` s, `web-based-seller` w where `product-codes` ='" + codesID +  "'and seller_id = w.id")
    while(rs.next())
      postings = postings :+ APPModel.WebPosting(rs.getString("price").toDouble,APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.URL")),rs.getString("s.url"), rs.getString("used"))
    stmt.close()
   db.close()

    postings
  }

  def retrieveCustomerReviews(codesID : String) : List[APPModel.CustomerReview] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var customerReviews : List[APPModel.CustomerReview] = List()

    val rs = stmt.executeQuery("select id, `title`,`review-text`, `date-added`, source from `customer-review` where `product-codes` = '" + codesID + "'")
    while(rs.next())
      customerReviews = customerReviews :+ APPModel.CustomerReview(rs.getString("title"),rs.getString("review-text"),rs.getString("date-added"),rs.getString("source"),rs.getString("id"))

    stmt.close()
    db.close()

    customerReviews
  }

  def retrieveExpertReviews(codesID : String) : List[APPModel.ExpertReview] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var expertReviews : List[APPModel.ExpertReview] = List()
    val rs = stmt.executeQuery("select url, title, `website-name` from `expert-review` where `product-codes` = '" + codesID + "'")
    while(rs.next())
      expertReviews = expertReviews :+ APPModel.ExpertReview(rs.getString("url"),rs.getString("title"),rs.getString("website-name"))

    stmt.close()
    db.close()

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
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var questions : List[APPModel.Question] = List()
    val rs = stmt.executeQuery("select id, `question-text` from question where `product-codes` = '" + codesID + "'")
    while(rs.next())
    {
      val answers = lookup("answer", "answer-text", "question-id", rs.getString("id"))
      questions = questions :+ APPModel.Question(rs.getString("question-text"),answers)
    }

    stmt.close()
    db.close()

    questions
  }

  def retrievePriceReductions(codesID : String) : List[APPModel.PriceReduction] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    var reductions : List[APPModel.PriceReduction] = List()
    val rs = stmt.executeQuery("select oldPrice,newPrice, logo, w.url from `price-reduction` p, `web-based-seller` w where `product-codes` ='" + codesID +  "'and sellerID = w.id")

    val productInfo = retrieveProductInfo(retrieveProductcodes(codesID))
    while(rs.next())
      reductions = reductions :+ APPModel.PriceReduction(APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("w.url")),productInfo,rs.getString("newPrice"),rs.getString("oldPrice"))


    stmt.close()
    db.close()

    reductions
  }

  def retrieveOffers(codesID : String) : List[APPModel.WebOffer] =
  {
    val db = DB.getConnection()(app)
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

    stmt.close()
    db.close()

    offers
  }
  def retrieveProduct(codes :  Map[String, String], username : String = ""): APPModel.Product =
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
    if (username != "")
      modifyFavorites(username, productInfo.category.name)
    APPModel.Product(productInfo,desc,postings,List(),customerReviews,expertReviews, offers, relatedProducts, questions, priceReductions)

  }

  def retrieveAllCategories() : List[APPModel.Category] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select parent_id, id, name, image from `product-category`")

    var results = List[APPModel.Category]()
    while(rs.next())
      results :+= APPModel.Category(rs.getString("id"),rs.getString("parent_id"),rs.getString("name"))

    db.close()
    rs.close()
    results
    //TODO: 1. see how to display images for categories 2. see how to make use of parent category
  }


  def retrieveAllCategoriesWithOffers() : List[(Int, APPModel.Category)] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select (select count(*) from offer_products,product where offer_products.`product-codes` = product.codes and `category-id` = pc.id) as `offerCount`, pc.parent_id, pc.id, pc.name, image from `product-category` pc where (select count(*) from offer_products,product where offer_products.`product-codes` = product.codes and `category-id` = pc.id) > 0")

    var results = List[(Int, APPModel.Category)]()
    while(rs.next())
      results :+= (rs.getString("offerCount").toInt,APPModel.Category(rs.getString("id"),rs.getString("parent_id"),rs.getString("name")))


    db.close()
    rs.close()
    results
    //TODO: 1. see how to display images for categories 2. see how to make use of parent category
  }


  def retrieveCategoryChildren(categoryName : String) :  List[String] =
  {


    var categoryQueue = Queue[String](categoryName)
    var results = List[String]()
    while(!categoryQueue.isEmpty)
    {
      val curCategory = categoryQueue.dequeue()
      val children = lookup("product-category","name", "parent_id", lookup("product-category", "id", "name", curCategory)(0))
      children.foreach(categoryQueue.enqueue(_))
      results +:= curCategory
    }

    results

  }
  def retrieveOffersInCategory(categoryName : String) : List[APPModel.WebOffer] =
  {
    val db = DB.getConnection()(app)
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

    stmt.close()
    db.close()

    offers

  }

  def retrievePriceReductionsInCategory(categoryName : String) : List[APPModel.PriceReduction] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select pc.name, `product-codes`, logo, w.url, off.id, oldPrice, newPrice " +
      "from product pr, `product-category` pc, `price-reduction` off ,`web-based-seller` w where " +
      "w.id = off.sellerID and `category-id` = pc.id and pr.codes = `product-codes` and pc.name = '" + categoryName + "'")

    var reductions : List[APPModel.PriceReduction] = List()
    while(rs.next())
    {
      val seller = APPModel.WebBasedSeller(rs.getString("logo"),rs.getString("logo"))
      val info = APPModel.ProductInfo(retrieveProductcodes(rs.getString("product-codes")),rs.getString("pc.name"),APPModel.Category("","",categoryName),"",List())
       reductions +:= APPModel.PriceReduction(seller,info,rs.getString("newPrice"),rs.getString("oldPrice"))
    }

    stmt.close()
    db.close()

    reductions

  }

  def retrieveOntologyTree(category : String): DataCollectionModel.OntologyTree =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val rs = stmt.executeQuery("select * from `ontology-nodes` where `category-id` = '" + category + "' ORDER BY parent" )

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
    stmt.close()
    db.close()

    DataCollectionModel.OntologyTree(rootNode,category)



  }

  def retrieveAllProductCodesInCategory(categoryID : String): List[Map[String, String]] =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement

    val rs = stmt.executeQuery("select codes from product  where `category-id` = " + categoryID )
    var result = List[String]()

    while(rs.next())
    {
      result :+= rs.getString("codes")
    }
    db.close()
    stmt.close()
    result.map(retrieveProductcodes(_))


  }
def retrieveProductsWithReviewSentences(category : String): List[Map[String, String]] =
{
  val db = DB.getConnection()(app)
  val stmt = db.createStatement

  val rs = stmt.executeQuery("select co.id from `product-codes` co where (select count(*) from `review-sentences` where review_ID in (select id from `customer-review` where  `product-codes` = co.id))")

  var result = List[String]()
  while(rs.next)
  {
    result +:= rs.getString("co.id")
  }
  stmt.close()
  db.close()
  result.map(retrieveProductcodes(_)).map(retrieveProduct(_)).filter(_.info.category.name == category).map(_.info.codes)


}
  def retrieveReveiwsSentences(codes : Map[String, String]) :  List[List[(CoreMap, WeightedGraph)]]=
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    val id = productExists(codes)
    val rs = stmt.executeQuery("select sentences, graphs from `review-sentences` r where r.`product-codes` = '" + id + "' LIMIT 100")
    var reviews = List[List[CoreMap]]()
    var reviewGraphs = List[List[(WeightedGraph#Node, WeightedGraph#Edge)]]()

    while(rs.next())
    {
      val st =  rs.getObject(1).asInstanceOf[Array[Byte]]
      val sentencesBAIS = new ByteArrayInputStream(st)
      val sentencesOIS = new ObjectInputStream(sentencesBAIS)
      val sentences =  sentencesOIS.readObject().asInstanceOf[List[CoreMap]]
      reviews :+= sentences

      val graph =  rs.getObject(2).asInstanceOf[Array[Byte]]
      val graphBAIS = new ByteArrayInputStream(graph)

      val graphOIS = new ObjectInputStream(graphBAIS) {
        override def resolveClass(desc: java.io.ObjectStreamClass): Class[_] = {
          try { Class.forName(desc.getName, false, getClass.getClassLoader) }
          catch { case ex: ClassNotFoundException => super.resolveClass(desc) }
        }
      }
      val graphs =  graphOIS.readObject().asInstanceOf[List[(WeightedGraph#Node, WeightedGraph#Edge)]]
      reviewGraphs :+= graphs
    }

    stmt.close()
    db.close()


    //val b =

    val k = reviewGraphs.map(_.map( y=> (new WeightedGraph(1).assignGraph(y._1.asInstanceOf[List[WeightedGraph#NodeImpl]], y._2.asInstanceOf[List[WeightedGraph#Edge]]))))
    reviews.zip(k).map(x => x._1.zip(x._2))

  }


  def login(username : String, pwd : String) : APPModel.User =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement

    val rs = stmt.executeQuery("select * from `user-account` where username = '" + username + "' and password = '" + pwd + "'")
    var user : APPModel.User= null
    if (rs.next())
      user = APPModel.User(rs.getString("username"),rs.getString("password"),rs.getString("firstname"),rs.getString("lastname"),rs.getString("email"))
    else
     user =  null

    stmt.close()
    db.close()

    user
  }


  def modifyFavorites(username : String, categoryName : String): Unit =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement


    var rs = stmt.executeQuery("select * from `favorite-categories` where username = '" + username+ "' and categoryName = '" + categoryName + "'")

    if (rs.next())
    {
      stmt.executeUpdate("UPDATE `favorite-categories` SET frequency = frequency + 1 where username = '" + username + "' and categoryName = '" + categoryName + "'")
    }
    else
    {
      rs = stmt.executeQuery("select count(*) from `favorite-categories` where username = '" + username + "'")
      rs.next()
      if (rs.getString("count(*)").toInt == 3)
      {
        rs = stmt.executeQuery("select min(frequency), categoryName from `favorite-categories` where username = '" + username + "'")
        rs.next()
        val oldCategoryName = rs.getString("categoryName")

        stmt.executeUpdate("UPDATE `favorite-categories` SET frequency = 1, categoryName = '" + categoryName + "' where categoryName = '" + oldCategoryName + "'")
      }
      else
      {
        insertQuery("favorite-categories", List("frequency", "categoryName", "username"), List("1", categoryName, username))
      }
    }

    stmt.close()
    db.close()
  }
  def retrieveCategory(categoryName : String) : APPModel.Category=
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement

    val rs = stmt.executeQuery("select * from `product-category` where name = '" + categoryName + "'")

    if (rs.next())
    {
      val id = rs.getString("id")
      val parentID = rs.getString("parent_id")
      val name = rs.getString("name")
      stmt.close()
      db.close()
      return APPModel.Category(id,parentID,name)
    }


    throw new Exception("category not found ")

  }
  def getFavoriteCategories(username : String)  =
  {
    lookup("favorite-categories","categoryName","username", username).map(retrieveCategory(_))
  }

  def modifyOntology(categoryID : String, ontologyTree : DataCollectionModel.OntologyTree, username : String): Unit =
  {
    val nodes = ontologyTree.getBFSNodes()
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    stmt.executeUpdate("UPDATE `ontology-nodes` set support = support -1 where `category-id` = '" + ontologyTree.category + "'")
    nodes.foreach(n =>
    {
      val rs = stmt.executeQuery("select * from `ontology-features` onf, `ontology-nodes` onn where onf.nodeID = onn.id and onn.`category-id`  = '" + ontologyTree.category + "' and onf.feature = '" + n.features(0) + "'")
      if (rs.next)
        stmt.executeUpdate("UPDATE `ontology-nodes` SET support = support + 2 where id in (select nodeID from `ontology-features` where feature = '" + n.features(0) + "')")
      else
      {
        val key = insertQuery("ontology-nodes",List("support", "category-id"),List("1",ontologyTree.category),true)
        insertQuery("ontology-features",List("feature", "nodeID"),List(n.features(0), key))
      }
    })

    db.close()
    stmt.close()

  }


  def cacheSentimentTree(ontologyTree : String, sentimentTree : String): Unit =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    println(ontologyTree)
    stmt.executeUpdate("Insert INTO `sentiment-cache` (`tree-hash`,`result`) VALUES (sha1( '" + ontologyTree + "'),'" + sentimentTree + "')")
    stmt.close()
    db.close()



  }


  def checkCache(ontologyTree : String): String =
  {
    val db = DB.getConnection()(app)
    val stmt = db.createStatement
    println("a" + ontologyTree)
    val rs = stmt.executeQuery("select result from `sentiment-cache` where `tree-hash` = sha1('" + ontologyTree + "')")

    if (rs.next())
    {
      val result = rs.getString("result")
      stmt.close()
      db.close()
      return result
    }
    else
    {

      stmt.close()
      db.close()
      ""
    }
  }

}
