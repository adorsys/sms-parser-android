#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :smsparser:clean \033[0m"
./gradlew :smsparser:clean --stacktrace
​
echo -e "\033[0;32m ./gradlew :smsparser:install \033[0m"
./gradlew :smsparser:build --stacktrace
​
echo -e "\033[0;32m ./gradlew :smsparser:bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_API_KEY -PdryRun=false \033[0m"
./gradlew :smsparser:bintrayUpload -PbintrayUser="$BINTRAY_USERNAME" -PbintrayKey="$BINTRAY_API_KEY" -PdryRun=false --stacktrace