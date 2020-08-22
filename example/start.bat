@ECHO OFF
set mypath=%cd%
ECHO Running draw from following list of names: "%mypath%\names.txt"
PAUSE
java -jar -DFILE_PATH="%mypath%\names.txt" spin-wheel-1.0-SNAPSHOT-jar-with-dependencies.jar
PAUSE