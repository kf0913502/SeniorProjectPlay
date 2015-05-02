package model

/**
 * Created by kkk on 3/16/2015.
 */
import play.api.libs.json.Json

package object DataCollectionModel {

  case class Product(codes: Map[String, String], name: String, categoryName: String = "DEFAULT", parentCategoryName: String = "")

  case class Desc(codes: Map[String, String], sellerURL: String, descText: String)

  case class WebPosting(codes: Map[String, String], price: String, sellerURL: String, postingURL: String)

  case class ExpertReview(codes: Map[String, String], reviewURL: String, title: String, websiteName: String)

  case class CustomerReview(codes: Map[String, String], title: String, var text: String, websiteName: String)

  case class ProductImage(codes: Map[String, String], URL: String, sellerURL : String)

  case class WebSeller(URL : String, logo : String, name : String)

  case class WebOffer(codes : List[Map[String, String]], sellerURL : String, price : String, desc : String, startDate : String, endDate : String, viewCount : String)

  case class WebPriceReduction(codes : Map[String, String], sellerURL : String, newPrice : String, oldPrice : String)

  case class Question(question : String, answers : List[String], productCodes : Map[String, String])
  case class Related(C1 : Map[String, String], C2 : Map[String, String])

  case class OntologyNode(children : List[OntologyNode], features : List[String], sentiment : Integer)
  implicit val ProductRead = Json.reads[Product]
  implicit val DescRead = Json.reads[Desc]
  implicit val WebPostingRead = Json.reads[WebPosting]
  implicit val ExpertReviewRead = Json.reads[ExpertReview]
  implicit val CustomerReviewRead = Json.reads[CustomerReview]
  implicit val ProductImageRead = Json.reads[ProductImage]
  implicit val WebSellerRead = Json.reads[WebSeller]
  implicit val OfferRead = Json.reads[WebOffer]
  implicit val relatedRead = Json.reads[Related]
  implicit val questionRead = Json.reads[Question]
  implicit val WebPriceReductionRead = Json.reads[WebPriceReduction]
  implicit val OntologyNodeRead = Json.reads[OntologyNode]
}