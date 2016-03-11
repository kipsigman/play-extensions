package kipsigman.play.service

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.service.UrlUtils
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader

@Singleton
class UrlService @Inject() (ws: WSClient) extends UrlUtils {
  
  private val HttpProtocol = "http://"
  
  def absoluteUrl(request: RequestHeader, relativeUrl: String): String = {
    HttpProtocol + request.host + relativeUrl
  }
  
  /**
   * Shortens a URL using a third party URL shortener.
   */
  def shortenUrl(url: String)(implicit ec: ExecutionContext): Future[String] = {

    // Use Google URL shortner: https://developers.google.com/url-shortener/v1/getting_started#shorten
    val googleApiUrl = "https://www.googleapis.com/urlshortener/v1/url"
    val json = Json.obj("longUrl" -> url)
    val shortUrlFuture = ws.url(googleApiUrl).post(json).map(response => {
      (response.json \ "id").as[String]
    })
    shortUrlFuture
  }
}