package model

import play.api.db._
import play.api.Application
import java.sql.Statement
/**
 * Created by kkk on 3/12/2015.
 */
class MySQLAssistant(app : Application) extends DBAssistant{

  val db = DB.getConnection()(app)
  val stmt = db.createStatement

  def createInsertQuery(TN : String, fields: List[String], values: List[String]) : String =
  {

    val fieldsStr = fields.map(x => "`" + x + "`").mkString(",")
    val valuesStr = values.map(x => "'" + x + "'").mkString(",")
    "INSERT INTO `" + TN + "` (" + fieldsStr + ") VALUES (" + valuesStr + ")"

  }
  def insertQuery(TN : String, fields: List[String], values: List[String], returnGeneratedKey : Boolean = false )
   = {

    stmt.executeUpdate(createInsertQuery(TN, fields, values), Statement.RETURN_GENERATED_KEYS)
    if (returnGeneratedKey){
      val rs = stmt.getGeneratedKeys()
      rs.next()
      rs.getInt(1).toString()
    }
    else ""
  }
}
