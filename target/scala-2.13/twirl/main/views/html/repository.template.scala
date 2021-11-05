
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

object repository extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[RepositoryDetails,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(repositoryDetails: RepositoryDetails):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
"""),_display_(/*3.2*/main("Repository")/*3.20*/ {_display_(Seq[Any](format.raw/*3.22*/("""


"""),format.raw/*6.1*/("""<div class="container">
    <div class="jumbotron jumbotron-fluid">
        <h3>Repository Details</h3>
    </div>
</div>

<div class="container">
<dl class="row">
    <dt class="col-sm-3"><h4>Repository name</h4></dt>
    <dd class="col-sm-9"><h4>"""),_display_(/*15.31*/repositoryDetails/*15.48*/.getRepository().getName()),format.raw/*15.74*/("""</h4></dd>

    <dt class="col-sm-3"><h4>Description</h4></dt>
    <dd class="col-sm-9"><h4>"""),_display_(/*18.31*/repositoryDetails/*18.48*/.getRepository().getDescription()),format.raw/*18.81*/("""</h4></dd>

    <dt class="col-sm-3"><h4>Repository URL</h4></dt>
    <dd class="col-sm-9"><h4><a href=""""),_display_(/*21.40*/repositoryDetails/*21.57*/.getRepository().getCloneUrl()),format.raw/*21.87*/("""">"""),_display_(/*21.90*/repositoryDetails/*21.107*/.getRepository().getCloneUrl()),format.raw/*21.137*/("""</a></h4></dd>

    <dt class="col-sm-3"><h4>Created At</h4></dt>
    <dd class="col-sm-9"><h4>"""),_display_(/*24.31*/repositoryDetails/*24.48*/.getRepository().getCreatedAt()),format.raw/*24.79*/("""</h4></dd>

    <dt class="col-sm-3 text-truncate"><h4>Updated At</h4></dt>
    <dd class="col-sm-9"><h4>"""),_display_(/*27.31*/repositoryDetails/*27.48*/.getRepository().getUpdatedAt()),format.raw/*27.79*/("""</h4></dd>

    <dt class="col-sm-3"><h4>Issues</h4></dt>
    <dd class="col-sm-9"><h4>"""),_display_(/*30.31*/repositoryDetails/*30.48*/.getRepository().getOpenIssues()),format.raw/*30.80*/("""</h4></dd>

    </dd>
</dl>
    <table class="table">
        <thead>
        <tr><th>Issue</th><th>Created At</th><th>State</th><th>URL</th>
        </thead>
        <tbody>
        """),_display_(/*39.10*/for(issue <- repositoryDetails.getIssues()) yield /*39.53*/ {_display_(Seq[Any](format.raw/*39.55*/("""
        """),format.raw/*40.9*/("""<tr><td>"""),_display_(/*40.18*/issue/*40.23*/.getTitle()),format.raw/*40.34*/("""</td><td>"""),_display_(/*40.44*/issue/*40.49*/.getCreatedAt()),format.raw/*40.64*/("""</td><td>"""),_display_(/*40.74*/issue/*40.79*/.getState()),format.raw/*40.90*/("""</td><td><a href=""""),_display_(/*40.109*/issue/*40.114*/.getUrl()),format.raw/*40.123*/("""">"""),_display_(/*40.126*/issue/*40.131*/.getUrl()),format.raw/*40.140*/("""</a></td></tr>
        """)))}),format.raw/*41.10*/("""
        """),format.raw/*42.9*/("""</tbody>
    </table>
</div>



<hr/>

""")))}),format.raw/*50.2*/("""
"""))
      }
    }
  }

  def render(repositoryDetails:RepositoryDetails): play.twirl.api.HtmlFormat.Appendable = apply(repositoryDetails)

  def f:((RepositoryDetails) => play.twirl.api.HtmlFormat.Appendable) = (repositoryDetails) => apply(repositoryDetails)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/repository.scala.html
                  HASH: 1e6fda65c7f060f79fbf3d213db7855b28025cf7
                  MATRIX: 923->1|1055->40|1082->42|1108->60|1147->62|1176->65|1452->314|1478->331|1525->357|1645->450|1671->467|1725->500|1857->605|1883->622|1934->652|1964->655|1991->672|2043->702|2166->798|2192->815|2244->846|2377->952|2403->969|2455->1000|2570->1088|2596->1105|2649->1137|2860->1321|2919->1364|2959->1366|2995->1375|3031->1384|3045->1389|3077->1400|3114->1410|3128->1415|3164->1430|3201->1440|3215->1445|3247->1456|3294->1475|3309->1480|3340->1489|3371->1492|3386->1497|3417->1506|3472->1530|3508->1539|3578->1579
                  LINES: 27->1|32->2|33->3|33->3|33->3|36->6|45->15|45->15|45->15|48->18|48->18|48->18|51->21|51->21|51->21|51->21|51->21|51->21|54->24|54->24|54->24|57->27|57->27|57->27|60->30|60->30|60->30|69->39|69->39|69->39|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|70->40|71->41|72->42|80->50
                  -- GENERATED --
              */
          