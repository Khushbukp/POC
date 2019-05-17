#!/usr/bin/env bash

gcloud config set project example-khushbu

# gcloud app create

mvn appengine:deploy -Dspring.profiles.active=dev -Dapp.stage.appEngineDirectory="src/main/appengine/dev/" -Dapp.deploy.projectId="example-khushbu" -Dapp.deploy.version="v1"
