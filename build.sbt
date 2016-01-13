name := "AkkaTestApp1"

version := "1.0"

scalaVersion := "2.10.5"

scalacOptions += "-target:jvm-1.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"    % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j"    % "2.2.3",
  "com.typesafe.akka" %% "akka-remote"   % "2.2.3",
  "com.typesafe.akka" %% "akka-agent"    % "2.2.3", 
  "com.typesafe.akka" %% "akka-testkit"  % "2.2.3" % "test"
)
