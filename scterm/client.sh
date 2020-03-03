#!/usr/bin/env bash

cd scterm-client || exit 1

if [ $# != 0 ]; then
  mvn exec:java $ARGS "-Dexec.args=$*"
else
  mvn exec:java $ARGS
fi
