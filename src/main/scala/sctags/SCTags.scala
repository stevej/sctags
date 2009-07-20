package sctags

import net.pmonk.optparse.Opt
import scala.collection.mutable.ListBuffer
import java.io.File
import java.io.PrintStream
import java.text.Collator

object SCTags extends Parsing with TagGeneration {
  var outputFile: String = "tags"
  var recurse = false
  var etags = false

  def main(args: Array[String]) {
    val options = List(
      Opt('f')
      .withArgument[String]
      .help("Write tags to specified file. Use \"-\" for stdout")
      .alias('o')
      .action { fname => outputFile = fname },
      Opt("recurse")
      .withArgument[Boolean]
      .help("Recurse into directories supplied on command line")
      .alias('R', true)
      .action { r => recurse = r },
      Opt("etags")
      .withArgument[Boolean]
      .help("Generate a TAGS file for emacsen")
      .alias('E', true)
      .action { e => etags = e }
    )

    val files = new ListBuffer[File]
    args.foreach { fname =>
      val file = new File(fname)
      if (file.isDirectory) {
        if (recurse) {
          files ++= FileUtils.listFilesRecursive(file, {(f: File) => f.getName.endsWith(".scala")})
        } else {
          System.err.println("Skipping directory " + fname)
        }
      } else {
        if (file.getName.endsWith(".scala")) {
          println("adding file: " + file.getName)
          files += file
        } else {
          System.err.println("Skipping file " + fname)
        }
      }
    }

    val tags: Seq[(String, Seq[Tag])] = files.map(f => (f.getPath, generateTags(parse(f))))

    val output = outputFile match {
      case "-" => Console.out
      case "tags" if etags =>  new PrintStream("TAGS")
      case x => new PrintStream(x)
    }

    if (etags) {
      (new ETags)(tags, output)
    } else {
      (new CTags)(tags, output)
    }
  }
}
