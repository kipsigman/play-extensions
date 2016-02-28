package kipsigman.play.mvc

import scala.concurrent.ExecutionContext

import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers.parse

object BodyParsers {
  
  val defaultMaxLength = 1024 * 1024 * 1
  
  /**
   * BodyParser for Codecanyon's HTML5 Image Upload library: http://codecanyon.net/item/html-5-upload-image-ratio-with-drag-and-drop/8712634
   * Parse the body as form url encoded and extracts AJAX image upload data.
   */
  def html5ImageUpload(maxLength: Int)(implicit ec: ExecutionContext): BodyParser[Html5ImageUpload] = {
    parse.urlFormEncoded(maxLength).map(formData =>
      Html5ImageUpload.parse(formData)
    )
  }
  
  /**
   * BodyParser for Codecanyon's HTML5 Image Upload library: http://codecanyon.net/item/html-5-upload-image-ratio-with-drag-and-drop/8712634
   * Parse the body as form url encoded and extracts AJAX image upload data.
   */
  def html5ImageUpload(implicit ec: ExecutionContext): BodyParser[Html5ImageUpload] = html5ImageUpload(defaultMaxLength)
}
