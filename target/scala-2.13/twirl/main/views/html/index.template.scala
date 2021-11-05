
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.api.data.Field
import play.data._
import play.core.j.PlayFormsMagicForJava._
import scala.jdk.CollectionConverters._

object index extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
  """),_display_(/*3.4*/main("Welcome to Play")/*3.27*/ {_display_(Seq[Any](format.raw/*3.29*/("""
"""),format.raw/*4.1*/("""<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <script language="JavaScript">
    function showInput() """),format.raw/*9.26*/("""{"""),format.raw/*9.27*/("""
        """),format.raw/*10.9*/("""document.getElementById('display').innerHTML =
                    document.getElementById("user_input").value;
    """),format.raw/*12.5*/("""}"""),format.raw/*12.6*/("""
  """),format.raw/*13.3*/("""</script>

</head>
<body>
<div class="container">
  <div class="jumbotron">
    <h1 >Gitterific</h1>
  </div>
</div>
<div class="container">
    <form action=""""),_display_(/*23.20*/routes/*23.26*/.GithubController.search()),format.raw/*23.52*/("""" method="post">
      <input type="text" class="input-lg" name="phrase">
      <button class="btn btn-info"><span class="glyphicon glyphicon-search"></span>Search</button>
    </form>

</div>
</body>
</html>

  """)))}),format.raw/*32.4*/("""
"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/index.scala.html
                  HASH: 9c7ec1a0ccdf0fb280216a262ad045302ff8e33c
                  MATRIX: 900->1|996->4|1025->8|1056->31|1095->33|1122->34|1272->157|1300->158|1336->167|1479->283|1507->284|1537->287|1724->447|1739->453|1786->479|2029->692
                  LINES: 27->1|32->2|33->3|33->3|33->3|34->4|39->9|39->9|40->10|42->12|42->12|43->13|53->23|53->23|53->23|62->32
                  -- GENERATED --
              */
          