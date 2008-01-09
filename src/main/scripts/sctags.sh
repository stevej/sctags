#!/bin/sh
SCTAGS_DIR="`dirname "$0"`"
scala -classpath "$SCTAGS_DIR/lib/scala-optparse-${scala-optparse.version}.jar:$SCTAGS_DIR/sctags-${version}.jar" net.pmonk.tools.sctags.SCTags "$@"
