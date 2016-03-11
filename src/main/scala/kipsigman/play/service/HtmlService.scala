package kipsigman.play.service

import java.io.StringReader
import javax.inject.Singleton
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

@Singleton
class HtmlService {
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  def repairBodyFragment(bodyFragment: String): String = {
    val doc = Jsoup.parseBodyFragment(bodyFragment)
    val bodyHtml = doc.body.html()
    val repairedBodyFragment = bodyHtml.replace("<body>", "").replace("</body>", "").trim()
    repairedBodyFragment
  }
  
  private def htmlDocFromBodyFragment(bodyFragment: String): String = {
    s"""<html><head></head><body>$bodyFragment</body></html>"""
  }
  
  def validateBodyFragment(bodyFragment: String): HtmlValidationResult = {
    val saxBuilder = new SAXBuilder()
    val html = htmlDocFromBodyFragment(bodyFragment)
    try {
      val doc = saxBuilder.build(new StringReader(html))
      HtmlValidationResult(true)
    } catch {
      case e: JDOMException => {
        HtmlValidationResult(false, Option(e.getMessage))
      }
    }
  }
}

case class HtmlValidationResult(valid: Boolean, errorMessage: Option[String] = None)