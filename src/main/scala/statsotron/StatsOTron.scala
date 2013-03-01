package statsotron

import akka.actor.Actor._
import datacollector.udp.{Stop, Start, UdpListener}
import java.io.File
import statsotron.datacollector.{Schedule, CollectionScheduler}
import statsotron.datacollector.jmx.RetrieverLifecycle
import java.util.concurrent.TimeUnit
import statsotron.output.DataPointOutput

class StatsOTron(xmlConfigDir: String, retrieverLifecycle: RetrieverLifecycle, dataPointOutput: DataPointOutput, listenerPort: Int, delay: Long, timeunit: TimeUnit) {

  val collectionScheduler = actorOf(new CollectionScheduler(retrieverLifecycle, dataPointOutput, delay, timeunit))
  val listener = actorOf(new UdpListener(dataPointOutput, listenerPort)).start()

  def start() {
    collectionScheduler.start()
    listener ! Start

    val xmlFiles = new File(xmlConfigDir).listFiles().filter(_.getName.endsWith(".xml"))
    for (xmlConfigFile <- xmlFiles) {
      collectionScheduler ! Schedule(xmlConfigFile)
    }
  }
  
  def stop() {
    collectionScheduler.stop()
    listener ! Stop
  }

}
