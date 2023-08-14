#!/bin/bash

#To run this test, you must have in the current directory:
#	EXFOR-2023-05-15.bck
#	trans.2313, trans.2314, trans.o093, trans.o094
#	EXFOR-2023-05-23.bck

echo "Script [$0] start `date +%F,%T` `uname -n`/`uname -s`"
java -version
t00=`date +%s`
set -x

#java -cp x4master.jar trans2master \
java -Xmx4000M -cp x4master.jar trans2master \
 -add19 -n3clean \
 -i:EXFOR-2023-05-15.bck \
 -n3set:20230523 -t:trans.2313 -t:trans.2314 -t:trans.o093 -t:trans.o094 \
 -h:REQUEST,644,20230523,142148,20230523,3 \
 -o:EXFOR-2023-05-23.new
errCode=$?
if [ $errCode -ne 0 ]; then 
  set +x
  echo "------Error=$errCode";
  echo "Please, check your java version.";
  java -version
  exit
fi

ls -la EXFOR-2023-05-23.bck EXFOR-2023-05-23.new
unix2dos EXFOR-2023-05-23.new
ls -la EXFOR-2023-05-23.bck EXFOR-2023-05-23.new
wc -l EXFOR-2023-05-23.bck EXFOR-2023-05-23.new

diff EXFOR-2023-05-23.bck EXFOR-2023-05-23.new >dif2.txt
if [ $? -ne 0 ]; then 
  if test "$OS" = "Windows_NT" ; then
    echo "--- Run Windows utility FC ---"
    FC EXFOR-2023-05-23.bck EXFOR-2023-05-23.new
    echo "--- FC finished ---"
  fi
fi
set +x
ndiff=`wc -l dif2.txt`
echo "Different lines: $ndiff"

t11=`date +%s`; dt0=$(($t11-t00))
echo "Script [$0] stop. `date +%F,%T`"
echo "Elapsed time: ${dt0}sec"
