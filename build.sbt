name := """gitterific"""

version := "2.8.x"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.6"

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies += guice

// https://mvnrepository.com/artifact/org.eclipse.mylyn.github/org.eclipse.egit.github.core
libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"

libraryDependencies ++= Seq(
  "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"
)

// disabled until https://github.com/playframework/playframework/issues/9845 is solved
//scalacOptions ++= List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
) 
