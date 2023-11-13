
@echo Script %0 start %date%,%time% on:%USERDOMAIN% user:%USERNAME%
@set startTime=%time%
@call :chkTime
@set startSec=%sec%

::To run this test you must have files in the current directory:
@call :chkFile EXFOR-2023-05-15.bck
@call :chkFile trans.2313
@call :chkFile trans.2314
@call :chkFile trans.o093
@call :chkFile trans.o094
@call :chkFile EXFOR-2023-05-23.bck

java -version

java -Xmx4000M -cp x4master.jar trans2master ^
 -add19 -n3clean ^
 -i:EXFOR-2023-05-15.bck ^
 -n3set:20230523 -t:trans.2313 -t:trans.2314 -t:trans.o093 -t:trans.o094 ^
 -h:REQUEST,644,20230523,142148,20230523,3 ^
 -o:EXFOR-2023-05-23.new
@set errCode=%ERRORLEVEL%

@if %errCode% NEQ 0 (
  @echo ------Error=%errCode%
  @echo Please, check your java version.
  java -version
  exit
)

dir EXFOR-2023-05-23.???
:: unix2dos EXFOR-2023-05-23.new
:: find /c /v "" EXFOR-2023-05-23.bck EXFOR-2023-05-23.new

@echo --- Run Windows utility FC ---
FC EXFOR-2023-05-23.bck EXFOR-2023-05-23.new
@echo --- FC finished ---

@echo Script %0 stop. %date%,%time%
@echo Start Time:   %startTime%
@echo Finish Time:  %time%
@call :chkTime
@set endSec=%sec%
@set /a dSec=%endSec%-%startSec%
@echo Elapsed time: %dSec% sec

@goto :EOF




:chkFile
@   if not  exist "%1" (
@	echo	?????????????? ERROR: file not found: "%1"
@	exit
    )
@   echo 	Checking file existence. OK: %1
@   exit/b

:chkTime
@   set tim=%TIME%
@   set hh=%tim:~0,2%
@   set mm=%tim:~3,2%
@   set ss=%tim:~6,2%
@   set /a sec=%hh%*3600+%mm%*60+%ss%
@   exit/b
