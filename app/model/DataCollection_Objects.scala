package model

/**
 * Created by kkk on 3/16/2015.
 */
package object DataCollectionModel {

  case class Product(codes: Map[String, String], name: String, categoryName: String = "DEFAULT", parentCategoryName: String = "")

  case class Desc(codes: Map[String, String], sellerURL: String, descText: String)

  case class WebPosting(codes: Map[String, String], price: String, sellerURL: String, postingURL: String)

  case class ExpertReview(codes: Map[String, String], reviewURL: String, title: String, websiteName: String)

  case class CustomerReview(codes: Map[String, String], title: String, text: String, websiteName: String)

  case class ProductImage(codes: Map[String, String], URL: String, sellerURL : String)

  case class WebSeller(URL : String, logo : String, name : String)

}