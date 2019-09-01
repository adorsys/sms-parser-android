#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :smsparser:clean \033[0m"
./gradlew :smsparser:clean

echo -e "\033[0;32m ./gradlew :smsparser:install \033[0m"
./gradlew :smsparser:install

if [ "$CI" == true ] && [ "$TRAVIS_PULL_REQUEST" == false ] && [ "$TRAVIS_BRANCH" == "master" ]; then

    echo -e "\033[0;32m ./gradlew :smsparser:bintrayUpload \033[0m"
    ./gradlew :smsparser:bintrayUpload

else
   echo -e "\033[0;32m Current branch is not master, will not upload to bintray. \033[0m"
fi