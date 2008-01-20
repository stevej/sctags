#!/bin/sh
SCTAGS_DIR="`dirname "$0"`"/../share/scala/lib
scala -classpath "$SCTAGS_DIR/scala-optparse.jar:$SCTAGS_DIR/sctags.jar" net.pmonk.tools.sctags.SCTags "$@"
