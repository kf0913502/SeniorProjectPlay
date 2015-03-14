package model

/**
 * Created by kkk on 3/14/2015.
 */
import scala._
package object scala
{
  type ProductCodes = Map[String, String]
}

abstract class DataObject
case class Product(codes : ProductCodes, name : String, price : Double, desc : String, images: String, dateAdded: String, source : String, category : Category) extends DataObject


case class ExpertReview(id : String, URL : String, title : String, codes : ProductCodes, webSiteName: String) extends DataObject


case class CustomerReview(id : String, text : String, dateAdded : String, sourceWebsite : String, codes : ProductCodes) extends DataObject


case class WebBasedSeller(id : String, logo : String, URL : String) extends DataObject

case class UserAccount(username : String, password : String, firstName : String, lastName : String, email : String, active : String) extends DataObject

case class Category(id : String, parentId : String, name : String) extends DataObject