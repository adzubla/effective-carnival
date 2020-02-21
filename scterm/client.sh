#!/usr/bin/env bash

cd scterm-client || exit 1

if [ $# != 0 ];
then
  ARGS="-Dexec.args=$1"
fi

mvn exec:java $ARGS
