package kipsigman.play.mvc

import java.io.ByteArrayInputStream

import scala.concurrent.ExecutionContext

import javax.inject.Inject
import javax.inject.Singleton

import play.api.http.ContentTypes
import play.api.libs.MimeTypes
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Action
import play.api.mvc.Controller

import kipsigman.play.service.S3Service

/**
 * Controller for serving/managing files on Amazon Web Services S3
 */
@Singleton
class S3Controller @Inject() (s3Service: S3Service)(implicit ec: ExecutionContext) extends Controller {
  
  def image(filename: String) = Action.async { implicit request =>
    s3Service.getImage(filename).map(bucketFile => {
      val inputStream = new ByteArrayInputStream(bucketFile.content)
      val dataContent: Enumerator[Array[Byte]] = Enumerator.fromStream(inputStream)
      val contentType = MimeTypes.forFileName(filename).getOrElse(ContentTypes.BINARY)
      Ok.chunked(dataContent).withHeaders(CONTENT_TYPE -> contentType)
    }).recover {
      case t: Throwable => NotFound
    }
  }
  
}