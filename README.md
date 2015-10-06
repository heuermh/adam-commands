adam-commands
=============

External commands in Java and Scala for ADAM: Genomic Data System.  Apache 2 licensed.


###Hacking adam-commands

Install

 * JDK 1.7 or later, http://openjdk.java.net
 * Scala 2.10.4 or later, http://www.scala-lang.org
 * Apache Maven 3.2.5 or later, http://maven.apache.org
 * Apache Spark 1.4.1 or later, http://spark.apache.org
 * ADAM: Genomic Data System 0.17.2-SNAPSHOT or later, https://github.com/bigdatagenomics/adam


To build

    $ mvn install


###Running adam-commands using ```adam-submit```

Apply this patch to [bigdatagenomics/adam](https://github.com/bigdatagenomics/adam) to allow main class to be overridden (see also pull request https://github.com/bigdatagenomics/adam/pull/854)

    $ git diff .
    diff --git a/bin/adam-submit b/bin/adam-submit
    index 15ea9cd..b7047c7 100755
    --- a/bin/adam-submit
    +++ b/bin/adam-submit
    @@ -78,6 +78,13 @@ CLI=$(ls "$ADAM_DIR" | grep cli)
     CLI_DIR="${ADAM_DIR}/${CLI}"
     ADAM_CLI_JAR=$(ls $CLI_DIR/*/adam-cli_2.1[01]-*.jar)
    
    +# Allow main class to be overridden
    +if [ -z "$ADAM_MAIN" ]; then
    +  ADAM_MAIN="org.bdgenomics.adam.cli.ADAMMain"
    +fi
    +echo "Using ADAM_MAIN=$ADAM_MAIN"
    +
     # Find spark-submit script
     if [ -z "$SPARK_HOME" ]; then
       SPARK_SUBMIT=$(which spark-submit)
     @@ -92,7 +99,7 @@ echo "Using SPARK_SUBMIT=$SPARK_SUBMIT"
    
     # submit the job to Spark
     "$SPARK_SUBMIT" \
      -  --class org.bdgenomics.adam.cli.ADAMMain \
      +  --class $ADAM_MAIN \
         --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
         --conf spark.kryo.registrator=org.bdgenomics.adam.serialization.ADAMKryoRegistrator \
         $SPARK_ARGS \


Specify ```ADAM_MAIN``` and add the adam-commands jar to the classpath using ```--jars```; external commands are listed in the usage text

    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ../adam/bin/adam-submit \
      --jars target/adam-commands_2.10-0.17.2-SNAPSHOT.jar
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args:
    
    
         e            888~-_              e                 e    e
        d8b           888   \            d8b               d8b  d8b
       /Y88b          888    |          /Y88b             d888bdY88b
      /  Y88b         888    |         /  Y88b           / Y88Y Y888b
     /____Y88b        888   /         /____Y88b         /   YY   Y888b
    /      Y88b       888_-~         /      Y88b       /          Y888b
    
    Usage: adam-submit [<spark-args> --] <adam-args>
    
    Choose one of the following commands:
    
    ADAM ACTIONS
                   depth : Calculate the depth from a given ADAM file, at each variant in a VCF
             count_kmers : Counts the k-mers/q-mers from a read dataset.
      count_contig_kmers : Counts the k-mers/q-mers from a read dataset.
               transform : Convert SAM/BAM to ADAM format and optionally perform read pre-processing transformations
              adam2fastq : Convert BAM to FASTQ files
                  plugin : Executes an ADAMPlugin
                 flatten : Convert a ADAM format file to a version with a flattened schema, suitable for querying with tools like Impala
    
    CONVERSION OPERATIONS
                vcf2adam : Convert a VCF file to the corresponding ADAM format
               anno2adam : Convert a annotation file (in VCF format) to the corresponding ADAM format
                adam2vcf : Convert an ADAM variant to the VCF ADAM format
              fasta2adam : Converts a text FASTA sequence file into an ADAMNucleotideContig Parquet file which represents assembled sequences.
           features2adam : Convert a file with sequence features into corresponding ADAM format
              wigfix2bed : Locally convert a wigFix file to BED format
    
    PRINT
                   print : Print an ADAM formatted file
             print_genes : Load a GTF file containing gene annotations and print the corresponding gene models
                flagstat : Print statistics on reads in an ADAM file (similar to samtools flagstat)
              print_tags : Prints the values and counts of all tags in a set of records
                listdict : Print the contents of an ADAM sequence dictionary
             allelecount : Calculate Allele frequencies
               buildinfo : Display build information (use this for bug reports)
                    view : View certain reads from an alignment-record file.
    
    EXTERNAL COMMANDS
        count_alignments : Counts the alignments in a read dataset.
    count_alignments_per_read : Counts the alignments per read in a read dataset.


Run external commands

    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ../adam/bin/adam-submit \
      --jars target/adam-commands_2.10-0.17.2-SNAPSHOT.jar \
      -- \
      count_alignments ../adam/adam-core/src/test/resources/small.sam
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args: "count_alignments" "../adam/adam-core/src/test/resources/small.sam"
    ...
    (1,20)


    $ ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain \
      ../adam/bin/adam-submit \
      --jars target/adam-commands_2.10-0.17.2-SNAPSHOT.jar \
      -- \
      count_alignments_per_read ../adam/adam-core/src/test/resources/small.sam
    
    Using ADAM_MAIN=com.github.heuermh.adam.commands.ADAMCommandsMain
    Using SPARK_SUBMIT=/usr/local/bin/spark-submit
    INFO ADAMMain: ADAM invoked with args: "count_alignments_per_read" "../adam/adam-core/src/test/resources/small.sam"
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
