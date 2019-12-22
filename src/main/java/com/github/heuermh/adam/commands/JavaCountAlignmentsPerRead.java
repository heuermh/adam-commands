/**
 * Copyright 2015-2020 held jointly by the individual authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.heuermh.adam.commands;

import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import org.apache.spark.rdd.RDD;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import org.bdgenomics.adam.api.java.JavaADAMContext;

import org.bdgenomics.adam.rdd.ADAMContext;

import org.bdgenomics.adam.rdd.read.AlignmentDataset;

import org.bdgenomics.formats.avro.Alignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Function1;
import scala.Option;
import scala.Tuple2;

/**
 * Count alignments per read Java ADAM command.
 *
 * @author  Michael Heuer
 */
public final class JavaCountAlignmentsPerRead implements Runnable, Serializable {
    private final String inputPath;
    private final Logger logger = LoggerFactory.getLogger(JavaCountAlignmentsPerRead.class);

    /**
     * Create a new count alignments per read runnable.
     *
     * @param inputPath input path
     */
    public JavaCountAlignmentsPerRead(final String inputPath) {
        this.inputPath = inputPath;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        SparkConf conf = new SparkConf().setAppName("adam: " + "java_count_alignments_per_read");
        if (conf.getOption("spark.master").isEmpty()) {
            conf.setMaster(String.format("local[%d]", Runtime.getRuntime().availableProcessors()));
        }
        SparkContext sc = new SparkContext(conf);
        try {
            run(sc);
        }
        catch (Throwable e) {
            System.err.println("Command body threw exception:\n" + e.toString());
            throw e;
        }
        finally {
            long totalTime = System.nanoTime() - start;
            logger.warn("Overall Duration: " + totalTime + "ns");
        }
    }

    private void run(final SparkContext sc) {
        ADAMContext ac = new ADAMContext(sc);
        JavaADAMContext jac = new JavaADAMContext(ac);
        AlignmentDataset alignments = jac.loadAlignments(inputPath);
        JavaRDD<Alignment> jrdd = alignments.jrdd();

        JavaRDD<String> contigNames = jrdd.map(new Function<Alignment, String>() {
                @Override
                public String call(final Alignment rec) {
                    return rec.getReadMapped() ? rec.getReadName() : "unmapped";
                }
            });

        JavaPairRDD<String, Integer> counts = contigNames.mapToPair(new PairFunction<String, String, Integer>() {
                @Override
                public Tuple2<String, Integer> call(final String readName) {
                    return new Tuple2<String, Integer>(readName, Integer.valueOf(1));
                }
            });

        JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer call(final Integer value0, final Integer value1) {
                    return Integer.valueOf(value0.intValue() + value1.intValue());
                }
            });

        reducedCounts.foreach(new VoidFunction<Tuple2<String, Integer>>() {
                @Override
                public void call(final Tuple2<String, Integer> count) {
                    System.out.println(count.toString());
                }
            });
    }

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new JavaCountAlignmentsPerRead(args[0]).run();
    }
}
