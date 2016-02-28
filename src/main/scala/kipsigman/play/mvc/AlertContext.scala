package kipsigman.play.mvc

/**
 * Alert context for use with Flash and other alerts.
 */
abstract class AlertContext(val name: String)

object AlertContext {
  case object Error extends AlertContext("error")
  case object Info extends AlertContext("info")
  case object Success extends AlertContext("success")
  case object Warning extends AlertContext("warning")
  
  val all: Set[AlertContext] = Set(Error, Info, Success, Warning)
  
  def apply(name: String): AlertContext = {
    all.find(s => s.name == name) match {
      case Some(flashKey) => flashKey
      case None => throw new IllegalArgumentException(s"Invalid AlertContext: $name")
    }
  }
  
  def bootstrapAlertClasses(key: AlertContext): String = {
    key match {
      case Error => "alert alert-danger"
      case Info => "alert alert-info"
      case Success =>	"alert alert-success"
      case Warning => "alert alert-warning"
    }
  }
  
  def bootstrapAlertClasses(keyName: String): String = {
    val key = AlertContext.apply(keyName)
    bootstrapAlertClasses(key)
  }
}