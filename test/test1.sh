#!/bin/bash

#To run this test, you must have in the current directory:
#	EXFOR-2023-04-29.bck
#	trans.c222
#	EXFOR-2023-05-15.bck
echo "Script [$0] start `date +%F,%T` `uname -n`/`uname -s`"
java -version
t00=`date +%s`
set -x

#java -cp x4master.jar trans2master \
java -Xmx4000M -cp x4master.jar trans2master \
 -i:EXFOR-2023-04-29.bck \
 -n3set:20230515 -t:trans.c222 \
 -h:REQUEST,1001,20230515,172831,20230515,3 \
 -o:EXFOR-2023-05-15.new
errCode=$?
if [ $errCode -ne 0 ]; then 
  set +x
  echo "------Error=$errCode";
  echo "Please, check your java version.";
  java -version
  exit
fi

ls -la EXFOR-2023-05-15.bck EXFOR-2023-05-15.new
unix2dos EXFOR-2023-05-15.new
ls -la EXFOR-2023-05-15.bck EXFOR-2023-05-15.new
wc -l EXFOR-2023-05-15.bck EXFOR-2023-05-15.new

diff EXFOR-2023-05-15.bck EXFOR-2023-05-15.new >dif1.txt
if [ $? -ne 0 ]; then 
  if test "$OS" = "Windows_NT" ; then
    echo "--- Run Windows utility FC ---"
    FC EXFOR-2023-05-15.bck EXFOR-2023-05-15.new
    echo "--- FC finished ---"
  fi
fi
set +x
ndiff=`wc -l dif1.txt`
echo "Different lines: $ndiff"

t11=`date +%s`; dt0=$(($t11-t00))
echo "Script [$0] stop. `date +%F,%T`"
echo "Elapsed time: ${dt0}sec"
