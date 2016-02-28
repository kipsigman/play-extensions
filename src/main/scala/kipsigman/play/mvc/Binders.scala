package kipsigman.play.mvc

import kipsigman.domain.entity.Status
import play.api.mvc.QueryStringBindable

object Binders {
  
  implicit def statusQueryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Status] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Status]] = {
      for {
        nameEither <- stringBinder.bind(key, params)
      } yield {
        nameEither match {
          case Right(name) => Right(Status(name))
          case _ => Left("Unable to bind Status")
        }
      }
    }
    override def unbind(key: String, value: Status): String = stringBinder.unbind(key, value.name)
  }
}