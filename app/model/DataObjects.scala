package model
  /**
   * Created by kkk on 3/14/2015.
   */



  abstract class DataObject

  case class Product(codes: Map[String, String], name: String,dateAdded: String, category: Category, images: List[String], descriptions: List[String], postings : List[Posting], reviews : List[Review]) extends DataObject

   class Posting( price : Double, sellerID : String = "")
  case class WebPosting( price : Double , seller : WebBasedSeller, URL : String) extends Posting(price)
  case class LocalPosting(price : Double, location : (Double, Double)) extends Posting(price)

  case class Review()
  case class ExpertReview( URL: String, title: String,  webSiteName: String, id: String = "") extends Review
  case class CustomerReview( text: String, dateAdded: String, sourceWebsite: String, id: String = "") extends Review


  case class WebBasedSeller( logo: String, URL: String, id: String = "") extends DataObject

  case class UserAccount(username: String, password: String, firstName: String, lastName: String, email: String, active: String) extends DataObject

  case class Category(id: String, parentId: String, name: String) extends DataObject

