scalaVersion := "2.12.6"

name := "autotest-tagless-talk-19Feb19"

scalacOptions ++= Seq(
  "-Ypartial-unification", // required by cats
  "-feature",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.6.0",
  "org.typelevel" %% "cats-laws" % "1.6.0"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "discipline" % "0.10.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,

  // note: needed to run legacy tests
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)


// kind projector plug in

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.8" cross CrossVersion.binary)

