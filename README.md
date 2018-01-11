adam-commands
=============

External commands in Java and Scala for ADAM: Genomic Data System.  Apache 2 licensed.


### Hacking adam-commands

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Scala 2.11.12 or later, http://www.scala-lang.org
 * Apache Maven 3.3.1 or later, http://maven.apache.org
 * Apache Spark 2.2.1 or later, http://spark.apache.org
 * ADAM: Genomic Data System 0.24.0-SNAPSHOT or later, https://github.com/bigdatagenomics/adam


To build

    $ mvn install


### Running adam-commands using ```adam-submit```

    $ cp target/adam-commands_2.11-0.24.0-SNAPSHOT.jar $ADAM_DIR
    $ cd $ADAM_DIR

To run the external commands in this repository via the ADAM command line, specify ```ADAM_MAIN``` and add the adam-commands jar
to the classpath with the Spark ```--jars``` argument.

Note the ```--``` argument separator between Spark arguments and ADAM arguments.

External commands are now listed in the usage text.

    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ./bin/adam-submit \
      --jars adam-commands_2.11-0.24.0-SNAPSHOT.jar \
      --
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args:
    
           e        888~-_         e            e    e
          d8b       888   \       d8b          d8b  d8b
         /Y88b      888    |     /Y88b        d888bdY88b
        /  Y88b     888    |    /  Y88b      / Y88Y Y888b
       /____Y88b    888   /    /____Y88b    /   YY   Y888b
      /      Y88b   888_-~    /      Y88b  /          Y888b
    
    Usage: adam-submit [<spark-args> --] <adam-args>
    
    Choose one of the following commands:

    ADAM ACTIONS
    ...
    
    EXTERNAL COMMANDS
             count_alignments : Counts the alignments in a read dataset.
    count_alignments_per_read : Counts the alignments per read in a read dataset.


Run external commands

    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ./bin/adam-submit \
      --jars adam-commands_2.11-0.24.0-SNAPSHOT.jar \
      -- \
      count_alignments adam-core/src/test/resources/small.sam
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args: "count_alignments" "adam-core/src/test/resources/small.sam"
    ...
    (1,20)


    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ./bin/adam-submit \
      --jars adam-commands_2.11-0.24.0-SNAPSHOT.jar \
      -- \
      count_alignments_per_read adam-core/src/test/resources/small.sam
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args: "count_alignments_per_read" "adam-core/src/test/resources/small.sam"
    ...
    (simread:1:237728409:true,1)
    (simread:1:195211965:false,1)
    (simread:1:163841413:false,1)
    (simread:1:231911906:false,1)
    (simread:1:26472783:false,1)
    (simread:1:165341382:true,1)
    (simread:1:240344442:true,1)
    (simread:1:50683371:false,1)
    (simread:1:240997787:true,1)
    (simread:1:14397233:false,1)
    (simread:1:207027738:true,1)
    (simread:1:20101800:true,1)
    (simread:1:5469106:true,1)
    (simread:1:186794283:true,1)
    (simread:1:189606653:true,1)
    (simread:1:101556378:false,1)
    (simread:1:37577445:false,1)
    (simread:1:89554252:false,1)
    (simread:1:153978724:false,1)
    (simread:1:169801933:true,1)
