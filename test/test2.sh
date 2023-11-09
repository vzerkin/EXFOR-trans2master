#!/bin/bash

user="$USER"
if [ "$user" = "" ] ; then user="${USERNAME}" ; fi
if [ "$user" = "" ] ; then user="${LOGNAME}"  ; fi
if [ "$user" = "" ] ; then user="`id -u -n`"  ; fi

echo "Script [$0] start `date +%F,%T` on:`uname -n`/`uname -s` user:$user"
java -version

chkFile() {
    if [ -f $1 ]; then 
	echo "---checking file existance [$1]: OK."
    else
	echo -e "\n___ERROR: file not found: [$1]"
	exit
    fi
}
chkFiles() {
    for file1 in $@; do
	chkFile $file1
    done
}

#To run this test you must have files in the current directory:
chkFiles EXFOR-2023-05-15.bck
chkFiles trans.2313 trans.2314 trans.o093 trans.o094
chkFiles EXFOR-2023-05-23.bck

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
set +x
if [ $errCode -ne 0 ]; then 
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
