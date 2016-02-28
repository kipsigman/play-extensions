package kipsigman.play.data.forms

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.data.FormError
import play.api.data.Forms.of
import play.api.data.Mapping
import play.api.data.format.Formats.stringFormat
import play.api.data.format.Formatter

object JavaTimeFormatters {
  
  private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }
  
  implicit val localDateFormatter: Formatter[LocalDate] = new Formatter[LocalDate] {

    def formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val format = Some(("format.localDate", Seq("yyyy-MM-dd")))
      
    def stringToLocalDate(str: String): LocalDate = LocalDate.parse(str, formatter)
      
    def bind(key: String, data: Map[String, String]) =
      parsing(stringToLocalDate, "error.localDate", Nil)(key, data)

    def unbind(key: String, value: LocalDate) = Map(key -> value.format(formatter))
  }
  
  val localDateMapping: Mapping[LocalDate] = of[LocalDate] as localDateFormatter
  
  implicit val localDateTimeFormatter: Formatter[LocalDateTime] = new Formatter[LocalDateTime] {

    def formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val format = Some(("format.localDateTime", Seq("yyyy-MM-dd'T'HH:mm:ss")))
      
    def stringToLocalDateTime(str: String): LocalDateTime = LocalDateTime.parse(str, formatter)
      
    def bind(key: String, data: Map[String, String]) =
      parsing(stringToLocalDateTime, "error.localDateTime", Nil)(key, data)

    def unbind(key: String, value: LocalDateTime) = Map(key -> value.format(formatter))
  }
  
  val localDateTimeMapping: Mapping[LocalDateTime] = of[LocalDateTime] as localDateTimeFormatter
  
}