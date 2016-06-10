package kipsigman.play.js

import play.api.i18n.Messages
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes
import play.twirl.api.JavaScript

/**
 * Contains I18N Messages for JavaScript.
 */
class JavaScriptMessages(allMessagesData: Map[String, Map[String, String]]) {

  // Message patterns have to escape quotes using double quotes, here we unescape them because we don’t support using quotes to escape format elements
  // TODO Also remove subformats
  private val allMessagesUnescaped: Map[String, Map[String, String]] =
    allMessagesData.mapValues(_.mapValues(_.replace("''", "'")))

  /**
   * Messages for each available lang of the application.
   */
  lazy val allMessages: Map[String, Map[String, String]] = for ((lang, msgs) <- allMessagesUnescaped) yield {
    lang match {
      // Do not merge with "default" if its "default.play"
      case "default.play" => lang -> allMessagesUnescaped.getOrElse("default.play", Map.empty)
      case _ => lang -> (
        allMessagesUnescaped.getOrElse("default.play", Map.empty) ++
        allMessagesUnescaped.getOrElse("default", Map.empty) ++
        extractCountry(lang).flatMap(country => allMessagesUnescaped.get(country)).getOrElse(Map.empty) ++
        msgs
      )
    }
  }

  final val allMessagesJson: JsValue = Json.toJson(allMessages)

  private val allMessagesCache: String = allMessagesJson.toString()

  private val messagesCache: Map[String, String] = allMessages.mapValues(map => formatMap(map))

  def messages(implicit messages: Messages): Map[String, String] = lookupLang(allMessages, messages)

  def messagesString(implicit messages: Messages): String = lookupLang(messagesCache, messages)

  /**
   * Generates a JavaScript function computing localized messages in the given implicit `Lang`.
   */
  def apply(namespace: Option[String] = None)(implicit messages: Messages): JavaScript = apply(namespace, messagesString)

  /**
   * Generates a JavaScript function computing localized messages in all the languages of the application.
   */
  def all(namespace: Option[String] = None): JavaScript = all(namespace, allMessagesCache)

  private def apply(namespace: Option[String], messages: String): JavaScript = {
    JavaScript(s""" #${namespace.map{_ + "="}.getOrElse("")}(function(u){function f(k){
          #var m;
          #if(typeof k==='object'){
            #for(var i=0,l=k.length;i<l&&f.messages[k[i]]===u;++i);
            #m=f.messages[k[i]]||k[0]
          #}else{
            #m=((f.messages[k]!==u)?f.messages[k]:k)
          #}
          #for(i=1;i<arguments.length;i++){
            #m=m.replace('{'+(i-1)+'}',arguments[i])
          #}
          #return m};
          #f.messages=$messages;
          #return f})()""".stripMargin('#'))
  }

  private def all(namespace: Option[String], messages: String): JavaScript = {
    // g(key): given a lang, try to find a key among all possible messages,
    //              will try lang, lang.language, default and finally default.play
    // h(key,args...): return the formatted message retrieved from g(lang,key)
    // f(lang,key,args...): if only lang, return anonymous function always calling h by prefixing arguments with lang
    //                      else, just call h with current arguments
    JavaScript(s""" #${namespace.map{_ + "="}.getOrElse("")}(function(u){function f(l,k){
          #function g(kg){
            #var r=f.messages[l] && f.messages[l][kg];
            #if (r===u&&l&&l.indexOf('-')>-1) {var lg=l.split('-')[0];r=f.messages[lg] && f.messages[lg][kg];}
            #if (r===u) {r=f.messages['default'] && f.messages['default'][kg];}
            #if (r===u) {r=f.messages['default.play'] && f.messages['default.play'][kg];}
            #return r;
          #}
          #function h(kh){
            #var m;
            #if(typeof kh==='object'){
              #for(var i=0,le=kh.length;i<le&&g(kh[i])===u;++i);
              #m=g(kh[i])||kh[0];
            #}else{
              #m=g(kh);
              #m=((m!==u)?m:kh);
            #}
            #for(i=1,le=arguments.length;i<le;++i){
              #m=m.replace('{'+(i-1)+'}',arguments[i])
            #}
            #return m;
          #}
          #if(k===undefined){
            #return h;
          #}else{
            #return h.apply({}, Array.prototype.slice.call(arguments, 1));
          #}
        #}
        #f.messages=$messages;
        #return f})()""".stripMargin('#'))
  }

  private def formatMap[A : Writes](map: Map[String, A]): String = Json.toJson(map).toString()

  private def extractCountry(lang: String): Option[String] = if (lang.contains("-")) Some(lang.split("-")(0)) else None

  private def lookupLang[A](data: Map[String, A], messages: Messages): A = {
    val lang = messages.lang
    // Try to get the messages for the lang
    data.get(lang.code)
      // If none, try to get it from its country
      .orElse(extractCountry(lang.code).flatMap(country => data.get(country)))
      // If none, fallback to default
      .orElse(data.get("default"))
      // If none, screw that, crash the system! It's your fault for no having a default.
      .getOrElse(sys.error(s"Lang $lang is not supported by the application. Consider adding it to your 'application.langs' key in your 'conf/application.conf' file or at least provide a default messages file."))
  }

}
