package kipsigman.play

import com.google.inject.AbstractModule
import javax.inject.Singleton
import kipsigman.play.service.ImageService
import kipsigman.play.service.S3Service

class PlayExtensionsModule() extends AbstractModule {
  
  def configure() {
    bind(classOf[ImageService])
    bind(classOf[S3Service])
  }
}