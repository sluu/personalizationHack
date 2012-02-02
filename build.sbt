//import com.gu.SbtDistPlugin

resolvers ++= Seq(
  "Sonatype OSS" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"
)

scalaVersion := "2.9.1"

//seq(sbtappengine.AppenginePlugin.webSettings :_*)



libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "org.slf4j" % "slf4j-simple" % "1.6.1",
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "commons-codec" % "commons-codec" % "1.5",
  "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.1",
  "org.scalatra" %% "scalatra" % "2.0.0",
  "org.scalatra" %% "scalatra-scalate" % "2.0.0",
  "com.gu.openplatform" %% "content-api-client" % "1.13",
  "net.liftweb" %% "lift-json" % "2.3"
)

// and use this version of jetty for jetty run
//libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "jetty"

//scalateTemplateDirectories in Compile <<= (baseDirectory) {
//  (basedir) => Seq(new File(basedir, "/src/main/webapp/templates"))
//}
