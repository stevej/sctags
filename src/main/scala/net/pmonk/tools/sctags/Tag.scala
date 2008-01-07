package net.pmonk.tools.sctags;

import scala.compat.StringBuilder

object Tag {
  def fieldString(field: Product2[String, String]) =
    field._1 + ":" + escapeValue(field._2)

  def escapeValue(value: String) = 
    (value.foldLeft(new StringBuilder) {(b, c) =>
      c match {
        case '\t' => b.append("\\t")
        case '\r' => b.append("\\r")
        case '\n' => b.append("\\n")
        case '\\' => b.append("\\\\")
        case x    => b.append(x)
      }
    }).toString
}

class Tag(val name: String, file: String, address: String, fields: Product2[String, String]*) {
  def fieldsString: String = 
    if (fields.isEmpty) {
      ""  
    } else {
      fields.map(Tag.fieldString).mkString(";\"\t", "\t", "")
    }
  override def toString = 
    name + "\t" + file + "\t" + address + fieldsString
}
