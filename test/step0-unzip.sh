#!/bin/bash
set -x

cat EXFOR-2023-04-29.zip_* >EXFOR-2023-04-29.zip
cat EXFOR-2023-05-15.zip_* >EXFOR-2023-05-15.zip
cat EXFOR-2023-05-23.zip_* >EXFOR-2023-05-23.zip

unzip EXFOR-2023-04-29.zip EXFOR-2023-04-29.bck
unzip EXFOR-2023-05-15.zip EXFOR-2023-05-15.bck
unzip EXFOR-2023-05-23.zip EXFOR-2023-05-23.bck
