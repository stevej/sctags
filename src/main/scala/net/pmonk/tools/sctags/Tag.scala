package net.pmonk.tools.sctags;

import scala.compat.StringBuilder

case class Tag(val name: String, pos: Position, fields: Product2[String, String]*) {
  private def fieldString(field: Product2[String, String]) =
    field._1 + ":" + escapeValue(field._2)

  private def escapeValue(value: String) =
    (value.foldLeft(new StringBuilder) {(b, c) =>
      c match {
        case '\t' => b.append("\\t")
        case '\r' => b.append("\\r")
        case '\n' => b.append("\\n")
        case '\\' => b.append("\\\\")
        case x    => b.append(x)
      }
    }).toString

  def fieldsString: String =
    if (fields.isEmpty) {
      ""  
    } else {
      fields.map(fieldString).mkString(";\"\t", "\t", "")
    }
  override def toString =
    name + "\t" + "\t" + pos.content + fieldsString
}
