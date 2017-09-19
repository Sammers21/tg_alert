#!/usr/bin/env bash

gradle clean shadowJar
java -jar ./build/libs/tg_alert-1.0-SNAPSHOT-all.jar