package kipsigman.play.mvc

import java.util.Base64

import org.apache.commons.io.FilenameUtils

/**
 * Parsing logic for Codecanyon's HTML5 Image Upload library: http://codecanyon.net/item/html-5-upload-image-ratio-with-drag-and-drop/8712634
 */
object Html5ImageUpload {
  
  object Key {
    val data = "data"
    val id = "id"
    val image = "image"
    val name = "name"
    val imageOriginalWidth = "imageOriginalWidth"
    val imageOriginalHeight = "imageOriginalHeight"
    val imageWidth = "imageWidth"
    val imageHeight = "imageHeight"
    
    val width = "width"
    val height = "height"
    
    val left = "left"
    val to = "to"
  }
    
  val base64Decoder = Base64.getMimeDecoder
    
  def parseDeleteId(formData: Map[String, Seq[String]]): Option[Int] = {
    val existingImageUrl = formData.get(Key.image).flatMap(_.headOption)
    
    // ex: /s3assets/images/88.jpg
    existingImageUrl.map(imageUrl =>
      FilenameUtils.getBaseName(imageUrl).toInt
    )
  }
  
  def parseUploadId(formData: Map[String, Seq[String]]): Option[Int] = {
    val existingImageUrl = formData.get(Key.name).flatMap(_.headOption)
    
    // ex: /s3assets/images/88.jpg
    existingImageUrl.flatMap(imageUrl => {
      val baseName = FilenameUtils.getBaseName(imageUrl)
      try {
        val id = baseName.toInt
        Option(id)
      } catch {
        case nfe: NumberFormatException => None
      }
    })
  }
    
  def parse(formData: Map[String, Seq[String]]): Html5ImageUpload = Html5ImageUpload(formData)
}

case class Html5ImageUpload(formData: Map[String, Seq[String]]) extends ImageUpload {
  import Html5ImageUpload._
  
  val dataCsvSeq = formData(Key.data).head.split(",")
    
  override val id: Option[Int] = parseUploadId(formData)
  override val byteArray: Array[Byte] = base64Decoder.decode(dataCsvSeq(1)) 
  override val mimeType: String = dataCsvSeq.head.replace("data:", "").replace(";base64", "")
  override val width: Int = formData(Key.width).head.toInt
  override val height: Int = formData(Key.height).head.toInt
  
  override def toString: String = s"Html5ImageUpload($id, <byteArray>, $mimeType, $width, $height)"
}