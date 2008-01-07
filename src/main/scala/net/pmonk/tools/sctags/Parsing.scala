package net.pmonk.tools.sctags

import scala.tools.nsc.{Global, CompilationUnits}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.ast.parser.SyntaxAnalyzer
import scala.tools.nsc.util.BatchSourceFile

import java.io.File;

object parse {
  def apply(af: AbstractFile)(implicit compiler: Global): Global#Tree = 
    apply(new compiler.CompilationUnit(new BatchSourceFile(af)))(compiler)

  def apply(f: File)(implicit compiler: Global): Global#Tree =
    apply(AbstractFile.getFile(f))(compiler)

  def apply(fname: String)(implicit compiler: Global): Global#Tree =
    apply(AbstractFile.getFile(fname))(compiler)

  def apply(cu: Global#CompilationUnit)(implicit compiler: Global): Global#Tree = {
    new compiler.Run
    val parser = new compiler.syntaxAnalyzer.UnitParser(cu.asInstanceOf[compiler.CompilationUnit])
    parser.compilationUnit
  }
}
