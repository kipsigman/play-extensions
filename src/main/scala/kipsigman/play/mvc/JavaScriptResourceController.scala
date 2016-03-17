package kipsigman.play.mvc

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions

import jsmessages.JsMessagesFactory
import play.api.i18n.I18nSupport
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.routing.JavaScriptReverseRoute
import play.api.routing.JavaScriptReverseRouter
import play.twirl.api.JavaScript

/**
 * Makes Play application resources available to JavaScript assets.
 *
 * To use, mixin this trait and define routes. For example:
 * {{{
 * object Application extends Controller with JavaScriptResourceController {
 *
 *   override protected def javaScriptReverseRoutes: Seq[JavaScriptReverseRoute] = Seq(
 *     routes.javascript.Assets.at,
 *     ...
 *   )
 *
 * }
 * }}}
 * {{{
 * GET  /app/config.js    controllers.AppController.configJs
 * GET  /app/messages.js  controllers.AppController.messagesJs
 * GET  /app/routes.js    controllers.AppController.routesJs
 * }}}
 */
trait JavaScriptResourceController extends Controller with I18nSupport {
  
  // Dependencies
  protected def config: Config
  
  protected def jsMessagesFactory: JsMessagesFactory
  
  
  // Naming configuration
  protected def namespace: Option[String] = None
  
  protected def configObjectName: String = "config"
  
  protected def messagesObjectName: String = "messages"
  
  protected def routesObjectName: String = "routes"
  
  protected def namespacedName(name: String): String = namespace.map(ns =>  s"$ns.$name").getOrElse(name)
  
  // Actions
  def configJs = Action {implicit request =>
    val objectName = namespacedName(configObjectName)
    val configJsonStr = config.root().render(ConfigRenderOptions.concise())
    val javascriptStr = s"var $objectName=$configJsonStr"
    val javascript = JavaScript(javascriptStr)
    Ok(javascript)
  }
  
  def messagesJs = Action {implicit request =>
    val objectName = s"window.${namespacedName(messagesObjectName)}"
    val messages = jsMessagesFactory.all.apply(Some(objectName))
    Ok(messages)
  }
  
  /**
   * Override with your app's routes
   */
  protected def javaScriptReverseRoutes: Seq[JavaScriptReverseRoute]
  
  def routesJs = Action {implicit request =>
    val objectName = namespacedName(routesObjectName)
    val javaScript = JavaScriptReverseRouter(objectName)(javaScriptReverseRoutes:_*)
    Ok(javaScript).as("text/javascript")
  }
}