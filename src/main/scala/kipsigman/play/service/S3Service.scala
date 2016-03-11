package kipsigman.play.service

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.typesafe.config.Config
import fly.play.s3.BucketFile
import fly.play.s3.BucketItem
import fly.play.s3.S3
import javax.inject.Inject
import javax.inject.Singleton
import play.api.Application
import org.slf4j.LoggerFactory

import kipsigman.domain.entity.Image
import kipsigman.play.mvc.ImageUpload

@Singleton
class S3Service  @Inject() (config: Config)(implicit ec: ExecutionContext, application: Application) {
  
  protected val logger = LoggerFactory.getLogger(getClass)
  
  protected val s3BucketImagesFolder = "images"
  
  protected lazy val s3BucketName = config.getString("aws.s3.bucketName")
  protected lazy val s3Bucket = S3(s3BucketName)
  protected lazy val s3BaseUrl = s"https://s3.amazonaws.com/${s3BucketName}/${s3BucketImagesFolder}"
  
  protected def s3ImageFilename(filename: String): String = s"${s3BucketImagesFolder}/${filename}"
  
  def getFile(filename: String): Future[BucketFile] = {
    s3Bucket.get(filename)
  }
  
  def getImage(imageFilename: String): Future[BucketFile] = {
    val filename = s3ImageFilename(imageFilename)
    getFile(filename)
  }
  
  def getImage(image: Image): Future[BucketFile] = {
    val imageFilename = image.filename.get
    getImage(imageFilename)
  }
  
  def list: Future[Iterable[BucketItem]] = {
    val result = s3Bucket.list
    result.foreach(bucketItems => {
      logger.debug(s"bucketItems=$bucketItems")
      bucketItems.foreach(bucketItem => {
        logger.debug(s"bucketItem=$bucketItem")
      })
    })
    
    result
  }
  
  def removeImage(image: Image): Future[Unit] = {
    val filename = s3ImageFilename(image.filename.get)
    s3Bucket.remove(filename)
  }
  
  def uploadImage(image: Image, imageUpload: ImageUpload): Future[Unit] = {
    val filename = s3ImageFilename(image.filename.get)
    val bucketFile = BucketFile(filename, image.mimeType, imageUpload.byteArray)
    
    s3Bucket.add(bucketFile)
  }
}