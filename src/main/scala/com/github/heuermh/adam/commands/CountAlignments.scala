/**
  *  Copyright 2015-2017 held jointly by the individual authors.
  * 
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  * 
  *  http://www.apache.org/licenses/LICENSE-2.0
  * 
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
package com.github.heuermh.adam.commands

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.read.AlignmentRecordRDD
import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.utils.cli._
import org.bdgenomics.utils.misc.Logging
import org.kohsuke.args4j.Argument

object CountAlignments extends BDGCommandCompanion {
  val commandName = "count_alignments"
  val commandDescription = "Counts the alignments in a read dataset."

  def apply(cmdLine: Array[String]) = {
    new CountAlignments(Args4j[CountAlignmentsArgs](cmdLine))
  }
}

class CountAlignmentsArgs extends Args4jBase with ParquetArgs {
  @Argument(required = true, metaVar = "INPUT", usage = "The ADAM, BAM or SAM file to count alignments from", index = 0)
  var inputPath: String = null
}

/**
 * Count alignments ADAM command.
 *  
 * @author  Michael Heuer
 */
class CountAlignments(protected val args: CountAlignmentsArgs) extends BDGSparkCommand[CountAlignmentsArgs] with Logging {
  val companion = CountAlignments

  def run(sc: SparkContext) {
    var alignments: AlignmentRecordRDD = sc.loadAlignments(args.inputPath)
    var rdd: RDD[AlignmentRecord] = alignments.rdd

    rdd.map(rec => if (rec.getReadMapped) rec.getContigName else "unmapped")
      .map(contigName => (contigName, 1))
      .reduceByKey(_ + _)
      .foreach(println)
  }
}
