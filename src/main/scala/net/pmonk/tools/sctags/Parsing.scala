package net.pmonk.tools.sctags

import scala.tools.nsc.{Global, CompilationUnits}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.ast.parser.SyntaxAnalyzer
import scala.tools.nsc.util.BatchSourceFile

import java.io.File;

trait Parsing { this: SCTags.type =>
  import compiler.syntaxAnalyzer._
  import compiler._
  object parse {
    def apply(af: AbstractFile): Tree =
      apply(new CompilationUnit(new BatchSourceFile(af)))

    def apply(f: File): Tree =
      apply(AbstractFile.getFile(f))

    def apply(fname: String): Tree =
      apply(AbstractFile.getFile(fname))

    def apply(cu: CompilationUnit): Tree = {
      new compiler.Run
      val parser = new UnitParser(cu)
      val tree = parser.compilationUnit
      compiler.analyzer.newNamer(compiler.analyzer.rootContext(cu, tree, false)).enterSym(tree)
      tree
    }
  }
}
