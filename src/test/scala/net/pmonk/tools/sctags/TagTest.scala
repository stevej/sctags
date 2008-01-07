package net.pmonk.tools.sctags

import org.specs._
import org.specs.runner.JUnit3

class tagSpecTest extends JUnit3(tagSpec)
object tagSpec extends Specification {
  "A tag" should {
    "escape tabs" in {
      val tag = new Tag("a","b","c", "d" -> "foo\tbar")
      val tagString = tag.toString
      tagString must include("foo\\tbar")
      tagString must notInclude("foo\tbar")
    }
    "escape line feeds" in {
      val tag = new Tag("a","b","c", "d" -> "foo\nbar")
      val tagString = tag.toString
      tagString must include("foo\\nbar")
      tagString must notInclude("foo\nbar")
    }
    "escape carriage returns" in {
      val tag = new Tag("a","b","c", "d" -> "foo\rbar")
      val tagString = tag.toString
      tagString must include("foo\\rbar")
      tagString must notInclude("foo\rbar")
    }
    "separate fields with tabs" in {
      val tag = new Tag("a","b","c","d"->"e")
      val tagString = tag.toString
      tagString mustEqual "a\tb\tc;\"\td:e"
    }
  }
}
