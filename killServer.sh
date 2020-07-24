#!/bin/bash
unameOut="$(uname -s)"
if [[ $unameOut == *"MINGW"* ]]; then
  PROCESSNO=$(tasklist | grep 'node' | head -n1 | awk '{print $2;}')
  PROCESSNO="//PID ${PROCESSNO} //F"
  taskkill $PROCESSNO 
else
  PROCESSNO=$(ps -ax | grep '\snode' | head -n1 | awk '{print $1;}')
  kill $PROCESSNO
fi
