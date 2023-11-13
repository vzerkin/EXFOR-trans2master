                      Nuclear Data Section (NDS)
             Department of Nuclear Sciences and Applications
                International Atomic Energy Agency (IAEA)
                Vienna International Centre, P.O. Box 100,
                        A-1400 Vienna, Austria
                Tel:(+43 1) 2600-21714; Fax:(+43 1) 26007
  
         WORKING MATERIALS ON THE MAINTENANCE OF EXFOR LIBRARY
  
    "Program trans2master for local maintenance of EXFOR Master file"
                Version for MS-Windows, Linux and MacOS.
                Created by Viktor Zerkin, IAEA-NDS, 2023
                Last modified: 2023-11-13 by V.Zerkin
_______________________________________________________________________________

CONTENT

 1) Source code: "src" directo3 java files, make file producing jar archive
    trans2master.java, exfor2subr.java, x4outfile.java, mk_x4master.sh
 2) Working package: directory "test" with program .jar, test scripts and data
 3) Help and how-to files
_______________________________________________________________________________

LICENSES

 1) See LICENSE.TXT
 2) For third party software, please see the README, "license.terms" files that 
    come in the associated directories.
_______________________________________________________________________________

DOWNLOAD

 1) Download the package "trans2master" from Internet:
    https://github.com/vzerkin/EXFOR-trans2master 
 2) Unpack package "EXFOR-trans2master-master.zip" to a working directory
_______________________________________________________________________________

SYSTEM ENVIRONMENT

 1) Open terminal
    Windows: <Win/R>cmd<Enter>
 2) Check Java installation
      $ java -version
      ----if OK---
      $ java -version
	java version "13.0.2" 2020-01-14
	Java(TM) SE Runtime Environment (build 13.0.2+8)
	Java HotSpot(TM) 64-Bit Server VM (build 13.0.2+8, mixed mode, sharing)
      ----if error---
      $ java -version
	'java' is not recognized as an internal or external command,
	operable program or batch file.
	2.err) Install Java
	     - Linux:Ubuntu
	       $ sudo apt-get install openjdk-8-jdk
	     - MacOS
	       $ brew install java
 3) If you want to use test1.sh and test2.sh
    install utility unix2dos
    Linux:
         $ sudo apt-get install unix2dos
    MacOS:
         $ brew install unix2dos
_______________________________________________________________________________

RUN TESTS

 0) Open Terminal and go to working directory
      > cd C:\wrk
      $ cd ~/Download/EXFOR-trans2master-master

 1) Run program to get help
      $ cd test
      $ java -cp x4master.jar trans2master
	...Help-text...

 2) Run test to merge EXFOR files
      $ java -cp x4master.jar trans2master -t:trans.e137 -t:trans.3208 -o:new1.x4

 3) Run prepared tests:
    ...scripts will run, produce new backup file and compare it with satndard...
    ...the total running time should be approximately 1-2 minutes....
    a) Windows:
      > step0-unzip.bat
      > test1.bat
      > test2.bat
    b) Linux/MacOS:
      $ set a+x *.sh
      $ ./step0-unzip.sh
      $ ./test1.sh
      $ ./test2.sh

 4) Run program with your data according to "help"
_______________________________________________________________________________


Please report setup/runtime errors to V.Zerkin@iaea.org, v.zerkin@gmail.com

_______________________________________________________________________________

         ALL PRODUCTS ON THIS PACKAGE ARE PROVIDED IN GOOD FAITH AND 
                    WITHOUT A WARRANTY OF ANY KIND.
