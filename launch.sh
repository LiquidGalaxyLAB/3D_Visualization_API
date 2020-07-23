#!/bin/bash

usage()
{
  echo "Usage: [-m] -i IP_ADDRESS -p PORT [-n NUMBER_SOCKETS] "
  exit 2
}

set_variable()
{
  local varname=$1
  shift
  eval "$varname=\"$@\""
}

#########################
# Main script starts here

FORPATH=$(bash --login -c 'env' | grep '^PATH=*')
export $FORPATH

MASTER='f'
NUMBER_SOCKETS=1
IP_ADDRESS="localhost"
PORT="3000"
while getopts 'mi:p:n:h' c
do
  case $c in
    m) set_variable MASTER 't' ;;
    i) set_variable IP_ADDRESS $OPTARG ;;
    p) set_variable PORT $OPTARG ;;
    n) set_variable NUMBER_SOCKETS $OPTARG ;;
    h) usage ;; esac
done


if [ "$MASTER" == "t" ]; then
  node app $PORT & sleep 1 
fi
echo "here"
DIMENSIONS=$(DISPLAY=:0 xdpyinfo | grep dimensions: | awk '{print $2}')
WIDTH=$(echo $DIMENSIONS | sed -E 's/x.*//')
HEIGHT=$(echo $DIMENSIONS | sed -E 's/.*x//')
n=1
while [ $n -le $NUMBER_SOCKETS ]
do
    POSITION=$((NUMBER_SOCKETS-n))
    if [ $((n%2)) -eq 0 ];then
        POSITION=$((NUMBER_SOCKETS+n-1))
    fi
    POSITION=$((POSITION/2))
    POSITION=$((POSITION*(WIDTH/NUMBER_SOCKETS)))

    unameOut="$(uname -s)"
    case "${unameOut}" in
        Linux*)     google-chrome "data:text/html,<html><body><script>window.moveTo($POSITION,0);window.resizeTo($(($WIDTH/$NUMBER_SOCKETS)),$HEIGHT);window.location='http://$IP_ADDRESS:$PORT';</script></body></html>";;
        Darwin*)    /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --app="data:text/html,<html><body><script>window.moveTo($POSITION,0);window.resizeTo($(($WIDTH/$NUMBER_SOCKETS)),$HEIGHT);window.location='http://$IP_ADDRESS:$PORT';</script></body></html>";;
        CYGWIN*)    "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --app="data:text/html,<html><body><script>window.moveTo($POSITION,0);window.resizeTo($(($WIDTH/$NUMBER_SOCKETS)),$HEIGHT);window.location='http://$IP_ADDRESS:$PORT';</script></body></html>";;
        MINGW*)     "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --app="data:text/html,<html><body><script>window.moveTo($POSITION,0);window.resizeTo($(($WIDTH/$NUMBER_SOCKETS)),$HEIGHT);window.location='http://$IP_ADDRESS:$PORT';</script></body></html>";;
        *)          echo "UNKNOWN:${unameOut}"
    esac
    # /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --app="data:text/html,<html><body><script>window.moveTo($POSITION,0);window.resizeTo($(($WIDTH/$NUMBER_SOCKETS)),$HEIGHT);window.location='http://$IP_ADDRESS:$PORT';</script></body></html>" 
    n=$((n+1))
done
