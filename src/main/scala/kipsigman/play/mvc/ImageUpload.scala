package kipsigman.play.mvc

trait ImageUpload {
  def id: Option[Int]
  def byteArray: Array[Byte]
  def mimeType: String
  def width: Int
  def height: Int
}