object SpinalVersion {
  val compilers = List("2.12.18", "2.11.12", "2.13.12")
  val compilerIsRC = false

  val isDev = false
  val isSnapshot = true
  private def snapshot = if (isSnapshot) "-SNAPSHOT" else ""
  private val major = "1.10.1"
  val all         = if(isDev) "dev" else s"$major$snapshot"
  val sim         = all
  val core        = all
  val lib         = all
  val ip          = all
  val debugger    = all
  val demo        = all
  val tester      = all
}
