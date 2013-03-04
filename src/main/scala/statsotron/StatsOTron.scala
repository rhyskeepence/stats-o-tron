package statsotron

import akka.actor.Actor._
import datacollector.udp.{Stop, Start, UdpListener}
import java.io.File
import statsotron.datacollector.{Schedule, CollectionScheduler}
import statsotron.datacollector.jmx.RetrieverLifecycle
import java.util.concurrent.TimeUnit
import statsotron.output.DataPointOutput

class StatsOTron(xmlConfigDir: String, retrieverLifecycle: RetrieverLifecycle, dataPointOutput: DataPointOutput, delay: Long, timeunit: TimeUnit) extends CollectorLifecycle {

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

class ListenOTron(dataPointOutput: DataPointOutput, listenerPort: Int) extends CollectorLifecycle {
  val listener = actorOf(new UdpListener(dataPointOutput, listenerPort)).start()

  def start() {
    listener ! Start
  }

  def stop() {
    listener ! Stop
  }
}