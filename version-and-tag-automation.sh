#!/usr/bin/env sh

set -e

function sedi {
  if [[ "$(uname)" == "Linux" ]]; then
    sed -i "$@"
  else
    sed -i "" "$@"
  fi
}

echo "Welcome to the automated versioning and tagging of the SMS-Parser Android project"

newVersionCode=0

# If versionCode is set with = (ex. versionCode = X) change {print $2} to {print $3}
currentVersionCode=$(awk '/ext.versionCode/ {print $3}' ./build.gradle)
echo "Current versionCode is: $currentVersionCode"

echo "Incrementing versionCode by 1 ..."

# If versionCode is set with = (ex. versionCode = X) change {print $2} to {print $3}
for entry in `awk '/ext.versionCode/ {print $3}' ./build.gradle`; do
    index=`echo ${entry}`
    sedi 's/ext.versionCode [0-9a-zA-Z -_]*/versionCode '$(($index + 1))'/' ./build.gradle
done

# If versionCode is set with = (ex. versionCode = X) change {print $2} to {print $3}
newVersionCode=$(awk '/ext.versionCode/ {print $3}' ./build.gradle)

echo "New versionCode is: $newVersionCode"

# If versionName is set with = (ex. versionName = "X.X.X") change {print $2} to {print $3}
currentVersionName=$(awk '/ext.versionName/ {print $3}' ./build.gradle)

echo "Current versionName is: $currentVersionName"

echo "Please type in the new versionName: "

read -r newVersionName

echo "Setting new versionName..."

sedi 's/ext.versionName [0-9a-zA-Z -_]*/ext.versionName "'"$newVersionName"'"/' ./build.gradle

echo "New versionName is: $newVersionName"

currentBranch=$(git symbolic-ref --short -q HEAD)
echo "$currentBranch"

git add build.gradle

echo "Staged changes in build.gradle"

git commit -m 'Bump up version'

echo "Commited build.gradle changes"

git tag "$newVersionName"

echo "Set new tag: $newVersionName"

git checkout master
git merge develop
git push origin

git checkout develop
git push origin

git push --tags

echo "Pushed changes and tag to $currentBranch and merged changes into master"

cat << "EOF"
______________________
< Who you Gonna Call >
----------------------
            \          __---__
                    _-       /--______
               __--( /     \ )XXXXXXXXXXX\v.
             .-XXX(   O   O  )XXXXXXXXXXXXXXX-
            /XXX(       U     )        XXXXXXX\
          /XXXXX(              )--_  XXXXXXXXXXX\
         /XXXXX/ (      O     )   XXXXXX   \XXXXX\
         XXXXX/   /            XXXXXX   \__ \XXXXX
         XXXXXX__/          XXXXXX         \__---->
 ---___  XXX__/          XXXXXX      \__         /
   \-  --__/   ___/\  XXXXXX            /  ___--/=
    \-\    ___/    XXXXXX              '--- XXXXXX
       \-\/XXX\ XXXXXX                      /XXXXX
         \XXXXXXXXX   \                    /XXXXX/
          \XXXXXX      >                 _/XXXXX/
            \XXXXX--__/              __-- XXXX/
             -XXXXXXXX---------------  XXXXXX-
EOF
