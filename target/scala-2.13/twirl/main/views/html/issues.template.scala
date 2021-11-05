
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

object issues extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[IssueWordStatistics,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.4*/(issues: IssueWordStatistics):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
  """),_display_(/*3.4*/main("Welcome to Play")/*3.27*/ {_display_(Seq[Any](format.raw/*3.29*/("""
"""),format.raw/*4.1*/("""<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  

</head>
<body>
<div class="container">
  <div class="jumbotron">
    <h1 >Gitterific</h1>
  </div>
</div>
<div class="container">
  <table class="table table-bordered">
  <thead>
    <tr>
      <th scope="col">Issue Keyword</th>
      <th scope="col">Count</th>
    </tr>
  </thead>
  <tbody>
  """),_display_(/*26.4*/for((key, value) <- issues.getWordfrequency()) yield /*26.50*/ {_display_(Seq[Any](format.raw/*26.52*/("""
    """),format.raw/*27.5*/("""<tr>
      
      <td>"""),_display_(/*29.12*/key),format.raw/*29.15*/(""" """),format.raw/*29.16*/("""</td>
      <td>"""),_display_(/*30.12*/value),format.raw/*30.17*/(""" """),format.raw/*30.18*/("""</td>
    </tr>
  """)))}),format.raw/*32.4*/("""
"""),format.raw/*33.1*/("""</tbody>
</table>
</div>
</body>
</html>

  """)))}),format.raw/*39.4*/("""
"""))
      }
    }
  }

  def render(issues:IssueWordStatistics): play.twirl.api.HtmlFormat.Appendable = apply(issues)

  def f:((IssueWordStatistics) => play.twirl.api.HtmlFormat.Appendable) = (issues) => apply(issues)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/issues.scala.html
                  HASH: a4ad137bd9b9e1aaf7e1b1a28f7b986b36e7d57e
                  MATRIX: 921->3|1044->33|1073->37|1104->60|1143->62|1170->63|1561->428|1623->474|1663->476|1695->481|1745->504|1769->507|1798->508|1842->525|1868->530|1897->531|1946->550|1974->551|2049->596
                  LINES: 27->1|32->2|33->3|33->3|33->3|34->4|56->26|56->26|56->26|57->27|59->29|59->29|59->29|60->30|60->30|60->30|62->32|63->33|69->39
                  -- GENERATED --
              */
          