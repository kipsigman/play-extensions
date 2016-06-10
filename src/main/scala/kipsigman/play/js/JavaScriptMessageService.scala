package kipsigman.play.js

import javax.inject.Inject
import javax.inject.Singleton
import play.api.i18n.MessagesApi

@Singleton
class JavaScriptMessageService @Inject() (messagesApi: MessagesApi) {
  
  /**
   * Returns all messages.
   */
  def all: JavaScriptMessages = new JavaScriptMessages(messagesApi.messages)

  /**
   * Returns messages that satisfy the filter predicate.
   * Example:
   *
   * {{{
   *   val javaScriptMessages = JavaScriptMessages.filtering(_.startsWith("error."))
   * }}}
   */
  def filtering(filter: String => Boolean): JavaScriptMessages = {
    new JavaScriptMessages(messagesApi.messages.mapValues(_.filterKeys(filter)))
  }

  /**
   * Returns messages whose keys are in `keys`
   * Example:
   *
   * {{{
   *   val javaScriptMessages = JavaScriptMessages.subset(
   *     "error.required",
   *     "error.number"
   *   )
   * }}}
   */
  def subset(keys: String*): JavaScriptMessages = filtering(keys.contains)
}