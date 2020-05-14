#!/usr/bin/env bash
echo "branch : $TRAVIS_BRANCH"
echo "pull_request : $TRAVIS_PULL_REQUEST"
echo "tag : $TRAVIS_TAG"
if [[ $TRAVIS_BRANCH = *"build"* || $TRAVIS_BRANCH = "master" ]] && [ "$TRAVIS_PULL_REQUEST" = 'false' ]; then
  openssl aes-256-cbc -K $encrypted_30d9879477f5_key -iv $encrypted_30d9879477f5_iv -in etc/codesigning.asc.enc -out etc/codesigning.asc -d
  gpg --fast-import etc/codesigning.asc
  if [ "$TRAVIS_TAG" = '' ];
  then
    echo "install and test"
    mvn install -B -q || exit 1
    if [ "$SKIP_DEPLOY" != 'true' ]; then
      echo "deploy"
      mvn -B deploy -T 4 -DskipTests=true -Dtravis --settings etc/deploy-settings.xml -U -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
    fi
  else
    if [[ $TRAVIS_TAG == *"build"* ]];
    then
      export TOCK_VERSION=$(cat pom.xml | grep "^    <version>.*</version>$" | awk -F'[><]' '{print $3}')
      echo "tock version : $TOCK_VERSION"
      echo "tock tag : $TRAVIS_TAG"
      echo "install and test"
      mvn versions:set -DnewVersion="$TRAVIS_TAG"
      mvn install -B -q || exit 1
      echo "deploy"
      mvn deploy -B -T 4 -DskipTests=true -Dmilestone --settings etc/deploy-settings.xml -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
    fi
  fi
  else
    if [ "$TRAVIS_PULL_REQUEST" = 'false' ];
    then
      echo "install and test"
      mvn install -B -q
    else
      echo "test PR : $TRAVIS_PULL_REQUEST"
      mvn install -B -q
    fi
fi