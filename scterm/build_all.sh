#!/usr/bin/env bash

mvn -Dmaven.test.skip=true clean install

cd scterm-server || exit 1
mvn dockerfile:build

cd ../scterm-client || exit 1
mvn dockerfile:build

cd ../scterm-responder || exit 1
mvn dockerfile:build
