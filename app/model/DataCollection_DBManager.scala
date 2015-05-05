package model

/**
 * Created by kkk on 3/8/2015.
 */





object DataCollection_DBManager {

  import play.api.Play.current
  import DataCollectionModel._
  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {

    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }


  def insertWebPosting(posting : DataCollectionModel.WebPosting): Unit =
  {
    mySqlAssistant.insertWebPosting(posting)
  }


  def insertWebSellerImage(img : DataCollectionModel.ProductImage): Unit =
  {
    mySqlAssistant.insertWebSellerImage(img)
  }

  def insertWebSellerDesc(desc : DataCollectionModel.Desc): Unit =
  {
    mySqlAssistant.inserWebSellerDesc(desc)
  }

  def insertWebSellerProduct(product : DataCollectionModel.Product): Unit =
  {
    mySqlAssistant.insertProduct(product)
  }

  def insertCustomerReview(review : DataCollectionModel.CustomerReview) =
  {

    mySqlAssistant.insertCustomerReview(review)

  }
  def insertExpertReview(review : DataCollectionModel.ExpertReview)
  {
    mySqlAssistant.insertExpertReview(review)

  }

  def insertWebSeller(seller : DataCollectionModel.WebSeller)
  {
    mySqlAssistant.insertWebSeller(seller)
  }


  def insertOffer(offer : DataCollectionModel.WebOffer)
  {
    mySqlAssistant.insertOffer(offer)
  }

  def insertRelated(related : DataCollectionModel.Related): Unit =
  {
    mySqlAssistant.insertRelated(related)
  }

  def insertQuestion(question : DataCollectionModel.Question): Unit =
  {
    mySqlAssistant.insertQuestion(question)
  }


  def insertWebPriceReduction(reduction: DataCollectionModel.WebPriceReduction): Unit =
  {
    mySqlAssistant.insertWebPriceReduction(reduction)
  }

  def insertOntologyTree(ontologyTree: OntologyTree): Unit =
  {
    mySqlAssistant.insertOntologyTree(ontologyTree)
  }

  def retrieveOntologyTree(codesID : String): DataCollectionModel.OntologyTree =
  {
    mySqlAssistant.retrieveOntologyTree(codesID : String)
  }
}
