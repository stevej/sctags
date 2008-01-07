package net.pmonk.tools.sctags

import scala.tools.nsc.Global;
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.symtab.Names

import scala.collection.mutable.ListBuffer

object generateTags {

  def apply[T <: Trees with Names](tree: T#Tree)(implicit compiler: T): Seq[Tag] = {


    class GenTagTraverser extends compiler.Traverser {
      val _tags = new ListBuffer[Tag]
      def tags: Seq[Tag] = _tags.toList

      import compiler._;

      def source(tree: Tree): Option[Tuple2[String,String]] = {
        tree.pos.source match {
          case Some(source) if tree.pos.lineContent != null =>
          Some((source.path, "/^" + tree.pos.lineContent + "$/"));
          case _ => None;
        }
      }

      def tag_(file: String, address: String)(name: Name, kind: char, fields: Tuple2[String, String]*) = {
        if (!name.decode.contains('$') && !name.decode.equals("this")) {
          val withKind = Seq.single("kind"->kind.toString).projection.append(fields)
          _tags += new Tag(name.decode, file, address, withKind: _*)
        }
      }

      override def traverse(t: Tree) {
        source(t) match {
          case Some((file, address)) =>
            def tag = tag_(file, address)_
            t match {
              case ModuleDef(mods, name, _) =>
                tag(name, 'o');
              case ClassDef(mods, name, tparams, _) => {
                var kind = if (mods.isCase) {
                  'C'
                } else if (mods.isTrait) {
                  'T'
                } else {
                  'c'
                }
                tag(name, kind);
              };
              case ValDef(mods, name, _, _) =>
                tag(name, 'v')
              case DefDef(mods, name, _, _, _, _) =>
                tag(name, 'm')
              case TypeDef(mods, name, _, _) =>
                tag(name, 't')
              case _ => ();
            }
          case _ => ();
        }
        super.traverse(t)
      }
    }

    val traverser = new GenTagTraverser
    traverser.traverse(tree.asInstanceOf[compiler.Tree])
    traverser.tags
  }
}
