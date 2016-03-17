# play-extensions
Additional functionality for use with Play Framework apps.

Feature Overview
* JavasScript resources: Make configuration, I18N messages, and routes available to your JavaScript assets
* S3 file & image management
* HTML5 Image upload (supports http://codecanyon.net/item/html-5-upload-image-ratio-with-drag-and-drop/8712634)
* Data/Form support for java.time
* Play/web extensions for Scala Domain model library: https://github.com/kipsigman/scala-domain-model

## Install
Add the following to your `build.sbt` file:

```scala
resolvers += Resolver.bintrayRepo("kipsigman", "maven")

libraryDependencies += "kipsigman" %% "play-extensions" % "0.2.4"
```

## JavaScript resources
1. Extend or mixin the JavaScriptResourceController

```scala
import scala.concurrent.ExecutionContext

import com.typesafe.config.Config
import javax.inject.Inject
import javax.inject.Singleton
import jsmessages.JsMessagesFactory
import kipsigman.play.mvc.JavaScriptResourceController
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import play.api.routing.JavaScriptReverseRoute

@Singleton
class Application @Inject() (
  messagesApi: MessagesApi,
  protected val config: Config,
  protected val jsMessagesFactory: JsMessagesFactory) extends Controller with JavascriptResourceController {
  
  // Set your routes
  override protected def javaScriptReverseRoutes: Seq[JavaScriptReverseRoute] = Seq(
    routes.javascript.Assets.at,
    routes.javascript.Application.someAjaxAction    
  )
  
  def someAjaxAction = Action {
    import play.api.json._
    val json = Json.obj("status" -> "success", "msg" -> "Item saved")
    Ok(json)
  }
}
```

2. Add to conf/routes

    ```
    # JavaScript resources
    GET  /resources/config.js    controllers.Application.configJs
    GET  /resources/messages.js  controllers.Application.messagesJs
    GET  /resources/routes.js    controllers.Application.routesJs
    ```
    
3. Add scripts to view

    ```
    <script type="text/javascript" src="@routes.Application.configJs"></script>
    <script type="text/javascript" src="@routes.Application.messagesJs"></script>
    <script type="text/javascript" src="@routes.Application.routesJs"></script>
    ```
    
4. Use in your JavaScript files

    ```javascript
    $.ajax({
      method : "POST",
      // Use routes resource
      url : routes.controllers.Application.someAjaxAction().url,
      dataType : 'json',
      success : function(data, textStatus, jqXHR) {
        if (data.status === "success") {
          // use messages resource
          var msg = messages("ajax.error");
          alert(msg);
        } else if (data.status === "error") {
          var msg = messages("ajax.success");
          alert(msg);
        }
      }
    });
    ```
    
5. (optional) Customize namespacing and object names
Sometimes you may prefer to define namespace and object names to avoid clashing with other JavaScript resources.

    ```scala
    class Application
    ...
    override protected def namespace: Option[String] = Some("myPlayApp")
  
    override protected def configObjectName: String = "theConfig"
    
    ```
    
    ```javascript
    var secret = myPlayApp.theConfig.play.crypto.secret;
    ```

## S3 file & image management

### Serve assets from S3
1. Add configuration for AWS S3
    * Set directly in application.conf/production.conf
  
    ```
    aws {
      accessKeyId=<anaccessid>
      secretKey=<asecretkey>
      s3 {
        bucketName=<abucketname>
      }
    }
    ```
    
    * or set environment variables (see reference.conf)
    
    ```sh
    export AWS_ACCESS_KEY_ID=xxx
    export AWS_SECRET_KEY=yyy
    export AWS_S3_BUCKET_NAME=zzz
    ```

2. Add routes S3Controller

```
GET  /s3assets/*filename         kipsigman.play.mvc.S3Controller.file(filename)
GET  /s3assets/images/*filename  kipsigman.play.mvc.S3Controller.image(filename)
```

## Image edit/upload

## Data/form support for java.time
