package model

/**
 * Created by kkk on 3/14/2015.
 */

sealed trait DataObjectType
case object Products extends DataObjectType
case object ExpertReviews extends DataObjectType
case object CustomerReviews extends DataObjectType
case object WebBasedSellers extends DataObjectType
case object UserAccounts extends DataObjectType
case object Category extends DataObjectType


object DataObjectFactory {
  def instantiate(objectType : DataObjectType)
  {
    objectType match
    {
      case Products => T = Product
    }
  }
}
