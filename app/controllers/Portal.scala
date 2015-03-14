
package controllers

import play.api._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import model._
import play.api.libs.json.{Json, JsError}

import model.jsonWrites._
object Portal extends Controller {


  def index() =  Action {
    Ok(views.html.DBTest("test"))
/*
    request =>
      val k = Json.parse("{\"info\":{\"codes\":{\"UPC\":\"2555\"},\"name\":\"iphone\",\"category\":{\"id\":\"\",\"parentId\":\"\",\"name\":\"phones\"},\"dateAdded\":\"2015-03-13 21:22:31.0\"},\"images\":[\"iphone.jpg\",\"iphone2.jpg\",\"iphone22.jpg\",\"iphone222.jpg\"],\"descriptions\":[\"Really fucking shiny22\"],\"webPosting\":[{\"price\":50000.0,\"seller\":{\"logo\":\"amazon.png\",\"URL\":\"www.Amazon.com\",\"id\":\"\"},\"URL\":\"www.amazon.com/iphoneLOL\"},{\"price\":50000.0,\"seller\":{\"logo\":\"amazon.png\",\"URL\":\"www.Amazon.com\",\"id\":\"\"},\"URL\":\"www.amazon.com/iphoneLOL\"}],\"localPosting\":[],\"customerReviews\":[{\"text\":\"Lolinator\",\"dateAdded\":\"2015-03-13 21:42:18.0\",\"sourceWebsite\":\"www.lol.com\",\"id\":\"\"},{\"text\":\"Lolinator\",\"dateAdded\":\"2015-03-13 21:42:38.0\",\"sourceWebsite\":\"www.lol.com\",\"id\":\"\"}],\"expertReviews\":[{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"},{\"URL\":\"www.lol.com\",\"title\":\"why iphones suck\",\"webSiteName\":\"Lolinator\",\"id\":\"\"}]}")
      Ok(views.html.DBTest(k.toString))

      request.
*/
  }


/*
  def login() {
    val userFormConstraints2 = Form(
      mapping(
        "pwd" -> nonEmptyText,
        "usr" -> nonEmptyText
      )(User.apply)(User.unapply)
    )


  }

  def loginSubmit() {

  }

*/


}