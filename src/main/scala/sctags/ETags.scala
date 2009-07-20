package sctags

import java.io.PrintStream

class ETags extends Tags {
  def formatTag(file: String, tag: Tag): String = {
    val pos = tag.pos
    "%s\177%s\01%s,%s\n".format(pos.content, tag.name, pos.line, pos.column)
  }

  def apply(files: Seq[(String, Seq[Tag])], output: PrintStream) {
    for ((file, tags) <- files) {
      val content = formatTags(file, tags)
      output.println("\f")
      output.println(file + "," + content.length)
      output.print(content.mkString)
    }
  }
}
