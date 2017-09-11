#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
    echo -e "Start clean module"
    ./gradlew :smsparser:clean
    echo -e "Finished clean"

    echo -e "Start install module"
    ./gradlew :smsparser:install
    echo -e "Finished install"

    echo -e "Start bintrayUpload"
    ./gradlew :smsparser:bintrayUpload
    echo -e "Finished bintrayUpload"
else
   echo "Current branch is not master, will not upload to bintray."
fi