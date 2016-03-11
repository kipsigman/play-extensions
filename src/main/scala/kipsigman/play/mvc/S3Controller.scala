package kipsigman.play.mvc

import java.io.ByteArrayInputStream

import scala.concurrent.ExecutionContext

import fly.play.s3.BucketFile
import javax.inject.Inject
import javax.inject.Singleton
import play.api.http.ContentTypes
import play.api.libs.MimeTypes
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result

import kipsigman.play.service.S3Service

/**
 * Controller for serving/managing files on Amazon Web Services S3
 */
@Singleton
class S3Controller @Inject() (s3Service: S3Service)(implicit ec: ExecutionContext) extends Controller {
  
  private def chunkFile(filename: String, bucketFile: BucketFile): Result = {
    val inputStream = new ByteArrayInputStream(bucketFile.content)
    val dataContent: Enumerator[Array[Byte]] = Enumerator.fromStream(inputStream)
    val contentType = MimeTypes.forFileName(filename).getOrElse(ContentTypes.BINARY)
    Ok.chunked(dataContent).withHeaders(CONTENT_TYPE -> contentType)
  }
  
  /**
   * Sends a chunked response of an S3 file
   * @param filename - Full filename with path for S3 bucket
   * @return
   */
  def file(filename: String) = Action.async { implicit request =>
    s3Service.getFile(filename).map(bucketFile => {
      chunkFile(filename, bucketFile)
    }).recover {
      case t: Throwable => NotFound
    }
  }
  
  /**
   * Sends a chunked response of an S3 image
   * @param filename - Filename/path residing under the "images" folder at the bucket root
   */
  def image(filename: String) = Action.async { implicit request =>
    s3Service.getImage(filename).map(bucketFile => {
      chunkFile(filename, bucketFile)
    }).recover {
      case t: Throwable => NotFound
    }
  }
  
}