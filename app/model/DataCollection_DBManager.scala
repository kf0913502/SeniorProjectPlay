package model

import java.io.{FileOutputStream, ObjectOutputStream}

import edu.stanford.nlp.util.CoreMap
import SentimentAnalysis.WeightedGraph

/**
 * Created by kkk on 3/8/2015.
 */


object DataCollection_DBManager {

  import play.api.Play.current
  import DataCollectionModel._
  val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]

  def insertUserAccount(userName: String, password: String, firstName: String, lastName: String, email: String)
  {
    val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]
    mySqlAssistant.insertUserAccount(userName, password, firstName, lastName, email)
  }

  def insertWebPosting(posting : DataCollectionModel.WebPosting): Unit =
  {
    val mySqlAssistant = DBAssistantFactory.instantiate(MySQL).asInstanceOf[MySQLAssistant]
    var codes = posting.codes
    if (posting.codes == null)
    {
      val m = MatcherVSM()
      val products = mySqlAssistant.searchProducts("")
      m.buildIndex(products.map(_.info.name.toLowerCase))
      val score = m.matchQuery(posting.name.toLowerCase())
      if (score._2 > 0.5)
      {
        codes = products.find(x => x.info.name.toLowerCase == score._1).get.info.codes
        mySqlAssistant.insertWebPosting(DataCollectionModel.WebPosting(codes, posting.price, posting.sellerURL, posting.postingURL, posting.used))
        return
      }

    }

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
    println("hello")
    mySqlAssistant.insertOntologyTree(ontologyTree)
  }

  def retrieveOntologyTree(category : String): DataCollectionModel.OntologyTree =
  {

    mySqlAssistant.retrieveOntologyTree(category : String)
  }


  def retrieveAllProductCodes(): List[Map[String, String]] =
  {

    mySqlAssistant.retrieveAllProductcodes()
  }

  def retrieveAllProductsInCategory(category : String): List[APPModel.CustomerReview] =
  {

    var products =  retrieveAllProductCodes().map(APP_DBManager.retrieveProduct(_)).filter(x => x.info.category.name == category)

    products.foreach(x => x.customerReviews.foreach(y => {y.text = y.text.replaceAll("\"", ""); y.title = y.title.replaceAll("\"", "") }))
    products.foreach(x => x.info.name = x.info.name.replaceAll("\"", ""))
    products.map(_.customerReviews).flatten
  }

  def insertProductSentiment(ontologyTree : OntologyTree, codes : Map[String, String]): Unit =
  {
    mySqlAssistant.insertProductSentiment(ontologyTree, codes)
  }

  def retrieveReveiwsSentencescodes (codes : Map[String, String]) : (List[List[CoreMap]], List[List[WeightedGraph]]) =
  {
    mySqlAssistant.retrieveReveiwsSentences(codes)
  }
}
