# all53e
java environment for QA

javac all53e/*.java
java -cp c:/projects/all53e; all53e.tester

it send command / receive results thru socket between your computer an board under test
scripts / commands might be build under buttons

specific counters may be displayed every .. seconds  

    "Parameters may be : ",
            "   -a < file name > : alias for IP addresses",
            "   -k < file for package approval buttons file >",
            "   -p < path > : path for all used files ( buttons, alias, etc )",
            "   -b < file for user buttons file >",
            "   -r < file for run parameter >",
            "   -h ( do history )",
            "   -s < number > : seed",
            "   -t < number > : connection timeout ( msec )",
            "   -d < pc | unix | linux > : device type ( default is UNIX )",
