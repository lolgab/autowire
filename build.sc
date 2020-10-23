import mill._
import scalalib._
import scalajslib._
import scalanativelib._
import publish._

val scala212 = "2.12.11"
val scala213 = "2.13.3"

val crossVersions = Seq(scala212, scala213)

def acyclicVersion(scalaVersion: String) = if(scalaVersion.startsWith("2.11.")) "0.1.8" else "0.2.0"

object autowire extends Module{
  object jvm extends Cross[autowireJvmModule](crossVersions:_*)
  class autowireJvmModule(val crossScalaVersion: String) extends AutowireModule{
    object test extends Tests with CommonTestModule
  }

  object js extends Cross[autowireJsModule](crossVersions:_*)
  class autowireJsModule(val crossScalaVersion: String) extends AutowireModule with ScalaJSModule{
    def scalaJSVersion = "1.2.0"
    object test extends CommonTestModule with Tests
  }

  object native extends Cross[autowireNativeModule]("2.11.12")
  class autowireNativeModule(val crossScalaVersion: String) extends AutowireModule with ScalaNativeModule{
    def scalaNativeVersion = "0.4.0-M2"
    object test extends CommonTestModule with Tests
  }
}

trait AutowireModule extends CrossScalaModule with PublishModule{
  def scalacPluginIvyDeps = Agg(ivy"com.lihaoyi::acyclic:${acyclicVersion(scalaVersion())}")
  def compileIvyDeps = Agg(
    ivy"com.lihaoyi::acyclic:${acyclicVersion(scalaVersion())}",
    ivy"org.scala-lang:scala-reflect:${scalaVersion()}"
  )
  def artifactName = "autowire"
  def publishVersion = "0.3.2"
  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "com.github.lolgab",
    url = "https://github.com/lolgab/autowire",
    licenses = Seq(License.MIT),
    scm = SCM(
      "git://github.com/lolgab/autowire.git",
      "scm:git://github.com/lolgab/autowire.git"
    ),
    developers = Seq(
      Developer("lihaoyi", "Li Haoyi","https://github.com/lihaoyi")
    )
  )
  def scalacOptions = T{
    super.scalacOptions() ++ Seq("-deprecation", "-feature")
  }
  def sources = T.sources(
    millSourcePath / "src"
  )
  def millSourcePath = super.millSourcePath / os.up
}
trait CommonTestModule extends ScalaModule with TestModule{
  def ivyDeps = T{ super.ivyDeps() ++ 
    Agg(
      ivy"com.lihaoyi::utest::0.7.4",
      ivy"com.lihaoyi::upickle::1.1.0"
    )
  }
  def scalacPluginIvyDeps = Agg(ivy"com.lihaoyi::acyclic:${acyclicVersion(scalaVersion())}")
  def compileIvyDeps = Agg(
    ivy"com.lihaoyi::acyclic:${acyclicVersion(scalaVersion())}",
    ivy"org.scala-lang:scala-reflect:${scalaVersion()}"
  )
  def testFrameworks = Seq("utest.runner.Framework")
}
