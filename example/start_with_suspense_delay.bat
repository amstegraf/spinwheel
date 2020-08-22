@ECHO OFF
set mypath=%cd%
ECHO Running draw from following list of names: "%mypath%\names.txt"
PAUSE
java -jar -DFILE_PATH="%mypath%\names.txt" -DSLEEP_ENABLED=TRUE -DSLEEP_PERIOD=1 -DMIN_SHUFFLES=1 -DMAX_SHUFFLES=5 -DMIN_WINNING_DRAWS=1 -DMAX_WINNING_DRAWS=5 spin-wheel-1.0-SNAPSHOT-jar-with-dependencies.jar
PAUSE