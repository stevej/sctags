package net.pmonk.tools.sctags

import scala.tools.nsc.{Settings, Global}
import scala.tools.nsc.reporters.StoreReporter

import scala.collection.mutable.ListBuffer

import net.pmonk.optparse._


import java.io.File
import java.io.PrintStream

import java.text.Collator

object SCTags extends ApplicationWithOptions
  with Parsing with TagGeneration
{

  import FileUtils._;

  var outputFile: String = "tags";
  var recurse = false;
  var etags = false

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
  );

  def error(str: String) = System.err.println("Error: " + str);
  val settings = new Settings(error);
  val reporter = new StoreReporter;
  val compiler = new Global(settings, reporter);

  def run(fnames: Seq[String]) {
    val files = new ListBuffer[File]
    fnames foreach { fname =>
      val file = new File(fname)
      if (file.isDirectory) {
        if (recurse)
          files ++= listFilesRecursive(file, {(f: File) => f.getName.endsWith(".scala")})
        else
          System.err.println("Skipping directory " + fname);
      } else {
        if (file.getName.endsWith(".scala"))
          files += file
        else
          System.err.println("Skipping file " + fname);
      }
    }

    val tags = files.map(f => (f.getPath, generateTags(parse(f))))
    val output = outputFile match {
      case "-" => Console.out
      case "tags" if etags =>  new PrintStream("TAGS")
      case x => new PrintStream(x)
    }

    if (etags) {
      ETags.generate(tags, output)
    } else {
      CTags.generate(tags, output)
    }
  }
}
