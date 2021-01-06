/**
  *  Copyright 2015-2021 held jointly by the individual authors.
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

import grizzled.slf4j.Logging
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.read.AlignmentDataset
import org.bdgenomics.formats.avro.Alignment
import org.bdgenomics.utils.cli._
import org.kohsuke.args4j.Argument

object CountAlignmentsPerRead extends BDGCommandCompanion {
  val commandName = "count_alignments_per_read"
  val commandDescription = "Counts the alignments per read in a read dataset."

  def apply(cmdLine: Array[String]) = {
    new CountAlignmentsPerRead(Args4j[CountAlignmentsPerReadArgs](cmdLine))
  }
}

class CountAlignmentsPerReadArgs extends Args4jBase {
  @Argument(required = true, metaVar = "INPUT", usage = "The ADAM, BAM or SAM file to count alignments per read from", index = 0)
  var inputPath: String = null
}

/**
 * Count alignments per read ADAM command.
 *  
 * @author  Michael Heuer
 */
class CountAlignmentsPerRead(protected val args: CountAlignmentsPerReadArgs) extends BDGSparkCommand[CountAlignmentsPerReadArgs] with Logging {
  val companion = CountAlignmentsPerRead

  def run(sc: SparkContext) {
    var alignments: AlignmentDataset = sc.loadAlignments(args.inputPath)
    var rdd: RDD[Alignment] = alignments.rdd

    rdd.map(rec => if (rec.getReadMapped) rec.getReadName else "unmapped")
      .map(readName => (readName, 1))
      .reduceByKey(_ + _)
      .foreach(println)
  }
}
