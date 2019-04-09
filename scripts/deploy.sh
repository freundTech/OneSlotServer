#!/usr/bin/env bash

source scripts/.private.sh
scp build/libs/oneslotserver-1.0-SNAPSHOT-all.jar minecraft@oneslotserver.fun:plugins
mcrcon -H oneslotserver.fun reload