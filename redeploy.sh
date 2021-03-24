#!/usr/bin/env bash

echo '> stash'
#git stash

echo '> pull'
#git pull

#cp ./src/main/resources/application.properties.prod ./src/main/resources/application.properties

./gradlew quarkusBuild --uber-jar
#ssh -p 4422 root@sh.asdk.io << EOF
#    ls
#EOF
scp -P4422 build/jksj-server-1.0-SNAPSHOT-runner.jar root@sh.asdk.io:/root/d/jksj/jksj-server-1.0-SNAPSHOT-runner.jar.new
