package kipsigman.play.mvc

import scala.concurrent.Future

import org.scalatestplus.play.PlaySpec
import org.slf4j.LoggerFactory

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Role
import kipsigman.domain.entity.UserBasic
import play.api.Configuration
import play.api.Environment
import play.api.i18n.DefaultLangs
import play.api.i18n.DefaultMessagesApi
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.libs.json.JsLookupResult.jsLookupResultToJsLookup
import play.api.libs.json.JsValue
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.status


class AjaxHelperSpec extends PlaySpec with Results with I18nSupport {
  override val messagesApi: MessagesApi = new DefaultMessagesApi(Environment.simple(), Configuration.reference, new DefaultLangs(Configuration.reference))
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  val user = UserBasic(Option(66), Option("Johnny"), Option("Utah"), "johnny.utah@fbi.gov", None, Set(Role.Member))
  implicit val userOption = Option(user)
  implicit val request = FakeRequest()
  
  private def assertOk(resultFuture: Future[Result]): JsValue = {
    status(resultFuture) mustEqual OK
      
    val content = contentAsJson(resultFuture)
    (content \ AjaxHelper.Status.key).as[String] mustBe AjaxHelper.Status.Success.name
    
    logger.debug(s"content=$content")
    content
  }
  
  private def assertOk(result: Result): JsValue = {
    val resultFuture = Future.successful(result)
    assertOk(resultFuture)
  }
  

  "entitySaveSuccessResultId" should {
    "return OK JSON" in {
      val result = AjaxHelper.entitySaveSuccessResult(99)
      val content = assertOk(result)
      
      (content \ AjaxHelper.Key.id).as[Int] mustBe 99
    }
  }
  
  "entitySaveSuccessResultEntity" should {
    "return OK JSON" in {
      val entity = Category(Option(1), "entertainment", 0)
      val result = AjaxHelper.entitySaveSuccessResult(entity)
      val content = assertOk(result)
      
      (content \ AjaxHelper.Key.id).as[Int] mustBe 1
      
      val entityJsValue = (content \ AjaxHelper.Key.entity)
      (entityJsValue \ "id").asOpt[Int] mustBe entity.id
      (entityJsValue \ "name").as[String] mustBe entity.name
      (entityJsValue \ "order").as[Int] mustBe entity.order
    }
  }

}