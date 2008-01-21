package net.pmonk.tools.sctags

import scala.tools.nsc.Global;
import scala.tools.nsc.symtab.Names

import scala.collection._

trait TagGeneration { this: SCTags.type =>
  import compiler._
  def generateTags(tree: Tree): Seq[Tag] = {
    class GenTagTraverser extends compiler.Traverser {
      val _tags = new mutable.ListBuffer[Tag]
      def tags: Seq[Tag] = _tags.toList

      import compiler._;

      def addTag(pos: Position, name: Name, fields: Map[String, String]) {
        if (!name.decode.contains('$') && !name.decode.equals("this")) {
          _tags += Tag(name.decode, pos, fields.toList: _*)
        }
      }

      def testMods[A](t: Tree, tests: Seq[(Modifiers => Boolean, A)]): Option[A] = {
        t match {
          case (mdef: MemberDef) => tests.find(_._1(mdef.mods)).map(_._2)
          case _ => None
        }
      }

      def access(t: Tree) = testMods(t,
        List(
          ((m: Modifiers) => m.isPrivate, "private"),
          ((m: Modifiers) => m.isPublic, "public"),
          ((m: Modifiers) => m.isProtected, "protected")))

      def implementation(t: Tree) = testMods(t,
        List(
          ((m: Modifiers) => m.isFinal, "final"),
          ((m: Modifiers) => m.isAbstract, "abstract"),
          ((m: Modifiers) => m.isSealed, "sealed")))

      def kind(t: Tree) = {
        t match {
          case ModuleDef(_,_,_) => Some("v")
          case ClassDef(mods,_,_,_) => testMods(t,
            List(
              ((m: Modifiers) => m.isCase, "C"),
              ((m: Modifiers) => m.isTrait, "T"),
              ((m: Modifiers) => true, "c")))
          case ValDef(_,_,_,_) => Some("v")
          case DefDef(_,_,_,_,_,_) => Some("m")
          case TypeDef(_,_,_,_) => Some("t")
          case _ => None
        }
      }

      override def traverse(t: Tree) {
        for (line <- t.pos.line;
             col <- t.pos.column) {
          val text = t.pos.lineContent
          var fields: immutable.Map[String, String] = immutable.Map.empty
          def addField(name: String, getter: Tree => Option[String]) {
            getter(t) match {
              case Some(value) => fields += (name -> value)
              case None => ()
            }
          }
          addField("access", access(_))
          addField("implementation", implementation(_))
          addField("kind", kind(_))
          val name = t match {
            case (dtree: DefTree) => Some(dtree.name)
            case _ => None
          }
          if (name.isDefined && fields.contains("kind")) {
            addTag(Position(line, col, text), name.get, fields)
          }
        }
        super.traverse(t)
      }
    }
    val traverser = new GenTagTraverser
    traverser.traverse(tree)
    traverser.tags
  }
}
