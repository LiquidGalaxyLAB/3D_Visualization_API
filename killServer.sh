#!/bin/bash

PROCESSNO=$(ps -ax | grep '\snode' | head -n1 | awk '{print $1;}')
kill $PROCESSNO