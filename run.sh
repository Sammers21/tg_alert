#!/usr/bin/env bash

./gradlew clean shadowJar
java -jar ./build/libs/tg_alert-1.0-SNAPSHOT-all.jar