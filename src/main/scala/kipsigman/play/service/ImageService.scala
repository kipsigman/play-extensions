package kipsigman.play.service

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.Content.ContentClass
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.ContentImage.DisplayType
import kipsigman.domain.entity.Image
import kipsigman.domain.repository.ImageRepository
import kipsigman.play.mvc.ImageUpload
import org.slf4j.LoggerFactory

@Singleton
class ImageService @Inject() (val imageRepository: ImageRepository, s3Service: S3Service)(implicit ec: ExecutionContext) {
  
  protected val logger = LoggerFactory.getLogger(getClass)
  
  def findImage(id: Int): Future[Option[Image]] = imageRepository.findImage(id)
  
  def deleteContentImage[T <: Content[T]](content: T, imageId: Int): Future[Int] = {
    imageRepository.findContentImage(content.contentClass, content.id.get, imageId) flatMap {
      case Some(contentImage) => {
        imageRepository.deleteContentImage(content.contentClass, contentImage.contentId, contentImage.image.id.get).flatMap(rowCount =>
          s3Service.removeImage(contentImage.image).map(x => rowCount)
        )    
      }
      case None => Future.successful(0)
    }
  }
  
  def findContentImages(contentClass: ContentClass, contentId: Int): Future[Seq[ContentImage]] =
    imageRepository.findContentImages(contentClass, contentId)
  
  def findContentImages(contentClass: ContentClass, contentIds: Set[Int]): Future[Seq[ContentImage]] =
    imageRepository.findContentImages(contentClass, contentIds)
    
  def findContentImages(contentClass: ContentClass, contentIds: Set[Int], displayType: DisplayType): Future[Seq[ContentImage]] =
    imageRepository.findContentImages(contentClass, contentIds, displayType)
  
  def saveContentImage[T <: Content[T]](content: T, image: Image, imageUpload: ImageUpload): Future[ContentImage] = {
    val contentImage = ContentImage(content.id.get, image, ContentImage.DisplayType.Primary, 0)
    imageRepository.saveContentImage(content.contentClass, contentImage).map(savedContentImage => {
      s3Service.uploadImage(savedContentImage.image, imageUpload)
      savedContentImage
    })
  }
  
}