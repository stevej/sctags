#!/bin/sh
java -classpath libs/scala-optparse-1.0.jar:sctags-1.0.jar:libs/scala-compiler-2.7.5.jar sctags.SCTags "$@"
