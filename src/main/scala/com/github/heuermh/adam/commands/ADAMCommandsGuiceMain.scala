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

import com.google.inject.{ AbstractModule, Guice, Injector }
import net.codingwell.scalaguice.ScalaModule
import net.codingwell.scalaguice.InjectorExtensions._
import org.bdgenomics.adam.cli.{ ADAMMain, CommandGroup }
import org.bdgenomics.adam.cli.ADAMMain.defaultCommandGroups

/**
 * Add external commands to ADAM command line interface via Guice injection.
 * 
 * @author  Michael Heuer
 */
object ADAMCommandsGuiceMain {
  def main(args: Array[String]) {
    val module = new AbstractModule with ScalaModule {
      override def configure() = {
        bind[List[CommandGroup]].toInstance(defaultCommandGroups.union(List(CommandGroup("EXTERNAL COMMANDS", List(CountAlignments, CountAlignmentsPerRead)))))
      }
    }
    val injector = Guice.createInjector(module)
    val commandGroups = injector.instance[List[CommandGroup]]
    new ADAMMain(commandGroups)(args)
  }
}
