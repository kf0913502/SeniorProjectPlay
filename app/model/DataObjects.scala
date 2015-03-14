package model
import play.api.libs.json.Json
  /**
   * Created by kkk on 3/14/2015.
   */



  abstract class DataObject

  case class Product(info : ProductInfo, images: List[String], descriptions: List[String],
                     webPosting : List[WebPosting], localPosting : List[LocalPosting], customerReviews : List[CustomerReview], expertReviews : List[ExpertReview])

  case class ProductInfo(codes : Map[String, String], name : String, category : Category, dateAdded: String = "" )
  case class WebPosting( price : Double , seller : WebBasedSeller, URL : String)
  case class LocalPosting(price : Double, location : Location)
  case class Location(longitutde : Double, latitude : Double)
  case class ExpertReview( URL: String, title: String,  webSiteName: String, id: String = "")
  case class CustomerReview( text: String, dateAdded: String, sourceWebsite: String, id: String = "")


  case class WebBasedSeller( logo: String, URL: String, id: String = "") extends DataObject

  case class UserAccount(username: String, password: String, firstName: String, lastName: String, email: String, active: String) extends DataObject

  case class Category(id: String, parentId: String, name: String) extends DataObject

  package object jsonWrites {
    implicit val categoryFormat = Json.writes[Category]
    implicit val productInfoFormat = Json.writes[ProductInfo]
    implicit val webSellerFormat = Json.writes[WebBasedSeller]
    implicit val customerReviewFormat = Json.writes[CustomerReview]
    implicit val expertReviewFormat = Json.writes[ExpertReview]
    implicit val locationFormat = Json.writes[Location]
    implicit val localPostingFormat = Json.writes[LocalPosting]
    implicit val webPostingFormat = Json.writes[WebPosting]
  }