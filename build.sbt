name := "AnalyticsApp"

version := "0.1"

scalaVersion := "2.12.5"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.google.cloud" % "google-cloud-bigquery" % "1.24.0"
)

mappings in(Compile, packageDoc) := Seq()

mappings in Universal += {
  val conf = (resourceDirectory in Compile).value / "application.conf"
  conf -> "/conf/application.conf"
}

mappings in Universal += {
  val ini = (resourceDirectory in Compile).value / "application.ini"
  ini -> "/conf/application.ini"
}

mappings in Universal += {
  val sh = (resourceDirectory in Compile).value / "purger.sh"
  sh -> "/conf/purger.sh"
}