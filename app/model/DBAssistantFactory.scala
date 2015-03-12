package model
import play.api.db._
import play.api.Application
import java.sql.Statement
/**
 * Created by kkk on 3/12/2015.
 */
sealed trait AssistantType
case object MySQL extends AssistantType
abstract class DBAssistant
object DBAssistantFactory {

  def instantiate(assistantType : AssistantType)(implicit app : Application) : DBAssistant =
  {
    assistantType match{
      case MySQL => new MySQLAssistant(app)
    }
  }

}
