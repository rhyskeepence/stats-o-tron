package sky.sns.statsotron

import akka.actor.Actor._
import java.io.File
import sky.sns.statsotron.datacollector.{Schedule, CollectionScheduler}
import sky.sns.statsotron.datacollector.jmx.RetrieverLifecycle
import java.util.concurrent.TimeUnit
import sky.sns.statsotron.output.DataPointOutput

class StatsOTron(xmlConfigDir: String, retrieverLifecycle: RetrieverLifecycle, dataPointOutput: DataPointOutput, delay: Long,  timeunit: TimeUnit) {

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
