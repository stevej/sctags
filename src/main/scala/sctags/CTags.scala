package sctags

import scala.util.Sorting
import java.io.PrintStream

class CTags extends Tags {

  private class CaseInsensitiveOrder(val self: String) extends Ordered[String] {
    // FIXME: can this be case-sensitive? would be faster.
    def compare(that: String) = self.compareToIgnoreCase(that)
  }

  private val header = Array(
    "!_TAG_FILE_FORMAT\t2\t//",
    "!_TAG_FILE_SORTED\t2\t/0=unsorted, 1=sorted, 2=sorted,casefold/"
  )

  def formatTag(file: String, tag: Tag): String = {
    val pos = "/^" + tag.pos.content.replace("\\","\\\\") + "$/"
    "%s\t%s\t%s%s".format(tag.name, file, pos, tag.fieldsString)
  }

  def apply(tags: Seq[(String, Seq[Tag])], output: PrintStream) {
    val tagStrings = tags.flatMap(tag => formatTags(tag._1, tag._2))
    Sorting.quickSort((tagStrings ++ header).toArray) { new CaseInsensitiveOrder(_) }
    tagStrings foreach { output.println(_) }
  }
}
