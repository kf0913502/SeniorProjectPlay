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
    //retrieveProduct(Map("UPC" -> "2555")).toString
    ""
  }

  def login(username : String, pwd : String) =
  {
    mySqlAssistant.login(username, pwd)
  }
  def insertUserAccount(user : APPModel.User)
  {

    mySqlAssistant.insertUserAccount(user)
  }



  def searchProducts(name : String): List[APPModel.Product]  =
  {
    mySqlAssistant.searchProducts(name)
  }

  def retrieveProduct(codes :  Map[String, String], username : String = ""): APPModel.Product =
  {
    mySqlAssistant.retrieveProduct(codes, username)
  }

  def retrieveAllCategories() : List[APPModel.Category] =
  {
    mySqlAssistant.retrieveAllCategories()
  }

  def retrieveOffersInCategory(name : String) : List[APPModel.WebOffer] =
  {
    var offers = List[List[APPModel.WebOffer]]()
    mySqlAssistant.retrieveCategoryChildren(name).foreach(offers +:= mySqlAssistant.retrieveOffersInCategory(_))

    offers.flatten
  }

  def retrievePricereductionsInCategory(category : String) : List[APPModel.PriceReduction] =
  {
    mySqlAssistant.retrieveCategoryChildren(category).map(mySqlAssistant.retrievePriceReductionsInCategory(_)).flatten

  }

  def getFavoriteCategories(username : String) :List[APPModel.Category]=
  {
    mySqlAssistant.getFavoriteCategories(username)
  }

  def modifyOntology(categoryID : String, ontologyTree : DataCollectionModel.OntologyTree, username : String): Unit =
  {
    mySqlAssistant.modifyOntology(categoryID, ontologyTree, username)
  }


  def cacheSentimentTree(ontologyTree : String, sentimentTree : String): Unit =
  {
    mySqlAssistant.cacheSentimentTree(ontologyTree,sentimentTree)
  }


  def checkCache(ontologyTree : String): String =
  {
    mySqlAssistant.checkCache(ontologyTree)
  }











}
