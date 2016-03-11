package kipsigman.play.service

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.AnyContent
import play.api.mvc.AnyContentAsEmpty
import play.api.test._
import play.api.libs.ws.WSClient

class UrlServiceSpec extends WordSpec with Matchers with MockitoSugar {

  val mockWsClient = mock[WSClient]
  val urlService = new UrlService(mockWsClient)

  "absoluteUrl" should {
    "make relativeUrl into a url friendly path" in {

      val relativeUrl = "/articles/kip-sigman-voted-most-worthless-employee-99"
      val headers = FakeHeaders(Seq(play.api.http.HeaderNames.HOST -> "www.news.com"))
      val request = FakeRequest[AnyContent]("GET", "http://www.news.com/articles/updateTag", headers, AnyContentAsEmpty)

      urlService.absoluteUrl(request, relativeUrl) shouldBe "http://www.news.com/articles/kip-sigman-voted-most-worthless-employee-99"
    }
  }

  "isValidUrl" should {
    "match valid URL" in {
      urlService.isValidUrl("http://www.thesith.com/vader.html") shouldBe true
      urlService.isValidUrl("www.thesith.com/vader.html") shouldBe false
      urlService.isValidUrl("xxx http://www.thesith.com/vader.html") shouldBe false
    }
  }

  "urlDecode" should {
    "decode string in URL" in {
      urlService.urlDecode("some+crap+to+be+in+a+querystring%3F%26") shouldBe "some crap to be in a querystring?&"
      urlService.urlDecode("http%3A%2F%2Fwww.thesith.com%2Fvader.html") shouldBe "http://www.thesith.com/vader.html"
    }
  }

  "urlEncode" should {
    "encode string for URL" in {
      urlService.urlEncode("some crap to be in a querystring?&") shouldBe "some+crap+to+be+in+a+querystring%3F%26"
      urlService.urlEncode("http://www.thesith.com/vader.html") shouldBe "http%3A%2F%2Fwww.thesith.com%2Fvader.html"
      urlService.urlDecode(urlService.urlEncode("http://www.thesith.com/vader.html")) shouldBe "http://www.thesith.com/vader.html"
    }
  }

  //  "shortenUrl" should {
  //    "return a shortened url" in {
  //      val shortUrlFuture = Urls.shortenUrl("http://www.thesith.com/vader.html")
  //      shortUrlFuture.map(_ shouldBe "http://goo.gl/tZChR"))
  //       
  //    }
  //  }

}