package sctags

import scalax.io.CommandLineParser

object Main {

  object Options extends CommandLineParser {
    val exclude = new StringOption('x', "exclude", "Exclude the given file") with AllowAll
    val version = new Flag('v', "version", "Show version info") with AllowNone

    override def helpHeader = """
    |  scalatags v0.1
    |""".stripMargin
  }


  def main(args: Array[String]) {

    Options.parseOrHelp(args) { cmd =>
      if(cmd(Options.version)) {
        //
      }
    }
  }
}
