scalaVersion := "2.12.6"

name := "final-tagless-talk-03Oct18"

scalacOptions ++= Seq("-Ypartial-unification","-feature","-language:higherKinds")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.4.0",
  "org.typelevel" %% "cats-free" % "1.4.0"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "discipline" % "0.10.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,

  // note: needed to run legacy tests
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)


// kind projector plug in

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.8" cross CrossVersion.binary)

