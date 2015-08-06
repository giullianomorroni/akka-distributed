name := """akka-distributed-cluster-java"""

version := "0.0.1"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-contrib" % "2.3.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4",
  "junit" % "junit" % "4.11" % "test",
  "com.github.scullxbones" %% "akka-persistence-mongo-casbah" % "0.4.1",
  "com.novocode" % "junit-interface" % "0.9" % "test->default")

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-a")

compileOrder := CompileOrder.JavaThenScala

fork in run := true