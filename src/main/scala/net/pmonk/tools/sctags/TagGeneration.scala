package net.pmonk.tools.sctags

import scala.tools.nsc.Global;
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.symtab.Names

import scala.collection.mutable.ListBuffer

trait TagGeneration { this: SCTags.type =>
  import compiler._
  def generateTags(tree: Tree): Seq[Tag] = {
    class GenTagTraverser extends compiler.Traverser {
      val _tags = new ListBuffer[Tag]
      def tags: Seq[Tag] = _tags.toList

      import compiler._;

      def tag_(pos: Position)(name: Name, kind: char, fields: Tuple2[String, String]*) = {
        if (!name.decode.contains('$') && !name.decode.equals("this")) {
          val withKind = Seq.singleton("kind"->kind.toString).projection.append(fields)
          _tags += Tag(name.decode, pos, withKind: _*)
        }
      }

      override def traverse(t: Tree) {
        for (line <- t.pos.line;
             col <- t.pos.column) {
          val text = t.pos.lineContent
          def tag = tag_(Position(line, col, text))_
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
        }
        super.traverse(t)
      }
    }
    val traverser = new GenTagTraverser
    traverser.traverse(tree)
    traverser.tags
  }
}
