#!/bin/bash
SERVICE_PATH="/home/logger/swan-log-collector"
mkdir -p ${SERVICE_PATH}/var
cd $SERVICE_PATH && bash control stop
