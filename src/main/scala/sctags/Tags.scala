package sctags

import java.io.PrintStream

trait Tags {
  /**
   * Formats an individual Tag from a file.
   */
  def formatTag(file: String, tag: Tag): String

  /**
   * Formats all Tags in a file.
   */
  def formatTags(file: String, tags: Seq[Tag]): Seq[String] = tags.map { formatTag(file, _) }

  /**
   * For each file in files, format the included tags and write to the output stream.
   */
  def apply(files: Seq[(String, Seq[Tag])], output: PrintStream)
}
