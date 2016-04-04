package kipsigman.play.mvc

import kipsigman.domain.entity._

import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results._

import play.api.libs.json.Json.toJsFieldJsValueWrapper

object AjaxHelper {
  
  abstract class Status(val name: String)
  object Status {
    val key = "status"
    case object Success extends Status("success")
    case object Error extends Status("error")
  }
  
  object Key {
    val entity = "entity"
    val errors = "errors"
    val id = "id"
  }
  
  
  def successResult(jsValue: JsValue)(implicit request: RequestHeader, user: Option[User]): Result = {
    Ok(jsValue)
  }
  
  def errorResult(jsValue: JsValue)(implicit request: RequestHeader, user: Option[User]): Result = {
    BadRequest(jsValue)
  }
  
  def notFoundResult(jsValue: JsValue)(implicit request: RequestHeader, user: Option[User]): Result = {
    NotFound(jsValue)
  }
  
  def contentSaveSuccessResult[T <: Content[T]](content: Content[T])
    (implicit request: RequestHeader, messages: Messages, user: Option[User]): Result = {
   
    val jsValue = Json.obj(Status.key -> Status.Success.name, Key.id -> content.id, "contentStatus" -> content.status.name)
    successResult(jsValue)
  }
  
  def entityNotFoundResult[T](clazz: Class[T], id: Int, additionalErrors: Seq[String] = Seq())
    (implicit request: RequestHeader, messages: Messages, user: Option[User]): Result = {
    
    val error = Messages("entity.find.error.notFound", clazz.getSimpleName, id)
    val errors = Seq(error) ++ additionalErrors
    val jsValue = Json.obj(Status.key -> Status.Error.name, Key.errors -> errors)
    notFoundResult(jsValue)
  }
  
  def entitySaveErrorResult(entity: IdEntity, errors: Seq[String])
    (implicit request: RequestHeader, messages: Messages, user: Option[User]): Result = {
    
    val jsValue = Json.obj(Status.key -> Status.Error.name, Key.id -> entity.id, Key.errors -> errors)
    errorResult(jsValue)
  }
  
  def entitySaveErrorResult(entity: IdEntity, formWithErrors: Form[_])
    (implicit request: RequestHeader, messages: Messages, user: Option[User]): Result = {
    
    val jsValue = Json.obj(Status.key -> Status.Error.name, Key.id -> entity.id, Key.errors -> formWithErrors.errorsAsJson)
    errorResult(jsValue)
  }
  
  def entitySaveSuccessResult(id: Int)
    (implicit request: RequestHeader, messages: Messages, user: Option[User]): Result = {
    
    val jsValue = Json.obj(Status.key -> Status.Success.name, Key.id -> id)
    successResult(jsValue)
  }
  
  def entitySaveSuccessResult[T <: IdEntity](entity: T)
    (implicit request: RequestHeader, messages: Messages, user: Option[User], entityWrites: Writes[T]): Result = {
    
    val entityJsValue = Json.toJson(entity)(entityWrites)
    val jsValue = Json.obj(Status.key -> Status.Success.name, Key.entity -> entityJsValue, Key.id -> entity.id)
    successResult(jsValue)
  }
}