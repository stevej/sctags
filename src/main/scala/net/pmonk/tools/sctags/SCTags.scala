package net.pmonk.tools.sctags

import scala.tools.nsc.{Settings, Global}
import scala.tools.nsc.reporters.StoreReporter

import scala.util.Sorting.quickSort;
import scala.collection.mutable.ListBuffer

import net.pmonk.optparse._


import java.io.File
import java.io.PrintStream

import java.text.Collator

object SCTags extends ApplicationWithOptions {

  class CaseInsensitiveOrder(val self: String) extends Ordered[String] {
    def compare(that: String) = self.compareToIgnoreCase(that)
  }

  val header = Array(
    new Tag("!_TAG_FILE_FORMAT","2","//"),
    new Tag("!_TAG_FILE_SORTED","2","/0=unsorted, 1=sorted, 2=sorted,casefold/")
  )

  import FileUtils._;

  var outputFile: String = "tags";
  var recurse = false;

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
      .action { r => recurse = r }
  );

  def error(str: String) = System.err.println("Error: " + str);
  val settings = new Settings(error);
  val reporter = new StoreReporter;
  implicit val compiler = new Global(settings, reporter);

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

    val tags = files.map(f => parse(f)).flatMap(t => generateTags(t))
    val tagStrings = (header ++ tags).map(_.toString).toArray
    quickSort(tagStrings)({s => new CaseInsensitiveOrder(s)})

    val output = outputFile match {
      case "-" => Console.out
      case x => new PrintStream(x)
    }

    tagStrings foreach {l => output.println(l)}
  }

}
