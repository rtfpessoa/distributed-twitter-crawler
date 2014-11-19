#!/bin/bash

if [ -z ${PSQL+x} ]; then
    if [ "$(uname)" == "Darwin" ]; then
        export PSQL="/Applications/Postgres.app/Contents/Versions/9.3/bin/psql -c";
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        export PSQL="sudo -u postgres psql -c";
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        echo "Do something under Windows NT platform"
    fi
fi

echo $PSQL
if [ $# == 0 ] || [ "$1" == "dtc" ] ; then
    echo "-> dtc"
    echo `$PSQL "select pg_terminate_backend(pid) from pg_stat_activity where datname='dtc';"`
    echo `$PSQL 'drop database dtc'`
    echo -e `$PSQL 'create database dtc'`'\n'
fi
