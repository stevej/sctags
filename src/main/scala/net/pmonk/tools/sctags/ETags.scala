package net.pmonk.tools.sctags;

import java.io.PrintStream

object ETags {

  def formatTags(tags: Seq[Tag]) = {
    val sb = new scala.compat.StringBuilder
    for (tag <- tags) {
      val pos = tag.pos
      val s = pos.content +  "\177" + tag.name + "\01" + pos.line + "," + pos.column + "\n"
      sb append s
    }
    sb.toString
  }

  def generate(files: Seq[(String, Seq[Tag])], output: PrintStream) {
    for ((file, tags) <- files) {
      val content = formatTags(tags)
      output.println("\f")
      output.println(file + "," + content.length)
      output.print(content)
    }
  }
}
