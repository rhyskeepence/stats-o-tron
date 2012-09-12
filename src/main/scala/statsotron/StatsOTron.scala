package statsotron

import akka.actor.Actor._
import java.io.File
import statsotron.datacollector.{Schedule, CollectionScheduler}
import statsotron.datacollector.jmx.RetrieverLifecycle
import java.util.concurrent.TimeUnit
import statsotron.output.DataPointOutput

class StatsOTron(xmlConfigDir: String, retrieverLifecycle: RetrieverLifecycle, dataPointOutput: DataPointOutput, delay: Long, timeunit: TimeUnit) {

  val collectionScheduler = actorOf(new CollectionScheduler(retrieverLifecycle, dataPointOutput, delay, timeunit))

  def start() {
    collectionScheduler.start()

    val xmlFiles = new File(xmlConfigDir).listFiles().filter(_.getName.endsWith(".xml"))
    for (xmlConfigFile <- xmlFiles) {
      collectionScheduler ! Schedule(xmlConfigFile)
    }
  }
  
  def stop() {
    collectionScheduler.stop()
  }

}
