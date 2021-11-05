// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  GithubController_0: controllers.GithubController,
  // @LINE:21
  Assets_1: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    GithubController_0: controllers.GithubController,
    // @LINE:21
    Assets_1: controllers.Assets
  ) = this(errorHandler, GithubController_0, Assets_1, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, GithubController_0, Assets_1, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.GithubController.index"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """search""", """controllers.GithubController.search(request:Request)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """getSearchResults""", """controllers.GithubController.getSearchResults"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """getRepositoryDetailsRender/""" + "$" + """userName<[^/]+>/""" + "$" + """repositoryName<[^/]+>""", """controllers.GithubController.getRepositoryDetails(userName:String, repositoryName:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """getRepositoryIssues/""" + "$" + """userName<[^/]+>/""" + "$" + """repositoryName<[^/]+>""", """controllers.GithubController.getIssues(userName:String, repositoryName:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_GithubController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_GithubController_index0_invoker = createInvoker(
    GithubController_0.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GithubController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """ Home page""",
      Seq()
    )
  )

  // @LINE:10
  private[this] lazy val controllers_GithubController_search1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("search")))
  )
  private[this] lazy val controllers_GithubController_search1_invoker = createInvoker(
    
    (req:play.mvc.Http.Request) =>
      GithubController_0.search(fakeValue[play.mvc.Http.Request]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GithubController",
      "search",
      Seq(classOf[play.mvc.Http.Request]),
      "POST",
      this.prefix + """search""",
      """Github""",
      Seq("""nocsrf""")
    )
  )

  // @LINE:13
  private[this] lazy val controllers_GithubController_getSearchResults2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("getSearchResults")))
  )
  private[this] lazy val controllers_GithubController_getSearchResults2_invoker = createInvoker(
    GithubController_0.getSearchResults,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GithubController",
      "getSearchResults",
      Nil,
      "GET",
      this.prefix + """getSearchResults""",
      """""",
      Seq()
    )
  )

  // @LINE:15
  private[this] lazy val controllers_GithubController_getRepositoryDetails3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("getRepositoryDetailsRender/"), DynamicPart("userName", """[^/]+""",true), StaticPart("/"), DynamicPart("repositoryName", """[^/]+""",true)))
  )
  private[this] lazy val controllers_GithubController_getRepositoryDetails3_invoker = createInvoker(
    GithubController_0.getRepositoryDetails(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GithubController",
      "getRepositoryDetails",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """getRepositoryDetailsRender/""" + "$" + """userName<[^/]+>/""" + "$" + """repositoryName<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:17
  private[this] lazy val controllers_GithubController_getIssues4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("getRepositoryIssues/"), DynamicPart("userName", """[^/]+""",true), StaticPart("/"), DynamicPart("repositoryName", """[^/]+""",true)))
  )
  private[this] lazy val controllers_GithubController_getIssues4_invoker = createInvoker(
    GithubController_0.getIssues(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GithubController",
      "getIssues",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """getRepositoryIssues/""" + "$" + """userName<[^/]+>/""" + "$" + """repositoryName<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:21
  private[this] lazy val controllers_Assets_versioned5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned5_invoker = createInvoker(
    Assets_1.versioned(fakeValue[String], fakeValue[Asset]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """ Map static resources from the /public folder to the /assets URL path""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_GithubController_index0_route(params@_) =>
      call { 
        controllers_GithubController_index0_invoker.call(GithubController_0.index)
      }
  
    // @LINE:10
    case controllers_GithubController_search1_route(params@_) =>
      call { 
        controllers_GithubController_search1_invoker.call(
          req => GithubController_0.search(req))
      }
  
    // @LINE:13
    case controllers_GithubController_getSearchResults2_route(params@_) =>
      call { 
        controllers_GithubController_getSearchResults2_invoker.call(GithubController_0.getSearchResults)
      }
  
    // @LINE:15
    case controllers_GithubController_getRepositoryDetails3_route(params@_) =>
      call(params.fromPath[String]("userName", None), params.fromPath[String]("repositoryName", None)) { (userName, repositoryName) =>
        controllers_GithubController_getRepositoryDetails3_invoker.call(GithubController_0.getRepositoryDetails(userName, repositoryName))
      }
  
    // @LINE:17
    case controllers_GithubController_getIssues4_route(params@_) =>
      call(params.fromPath[String]("userName", None), params.fromPath[String]("repositoryName", None)) { (userName, repositoryName) =>
        controllers_GithubController_getIssues4_invoker.call(GithubController_0.getIssues(userName, repositoryName))
      }
  
    // @LINE:21
    case controllers_Assets_versioned5_route(params@_) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned5_invoker.call(Assets_1.versioned(path, file))
      }
  }
}
