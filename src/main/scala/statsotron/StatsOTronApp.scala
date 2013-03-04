package statsotron

import statsotron.datacollector.jmx.JmxRetrieverLifecycle
import org.noggin.config.xml.DataCollectorConfigReader
import java.util.concurrent.TimeUnit._
import java.util.concurrent.{CountDownLatch, Executors}
import output.mongo.{MongoDataPointOutput, MongoStorage}

object StatsOTronApp extends App {

  val usage = "Usage: statsotron mongo-host mongo-port [-listen udp-listener-port] [-poll path-to-xml-config-dir]\n" +
    "\tEither -listen or -poll are required."

  args.toList match {
    case mongoHost :: mongoPort :: "-poll" :: xmlConfigDir :: Nil =>
      start(mongoHost, mongoPort.toInt, buildPoller(xmlConfigDir))

    case mongoHost :: mongoPort :: "-listen" :: listenPort :: Nil =>
      start(mongoHost, mongoPort.toInt, buildListener(listenPort.toInt))

    case _ =>
      println(usage)
  }

  def start(mongoHost: String, mongoPort: Int, buildCollector: MongoDataPointOutput => CollectorLifecycle) {
    val mongoStorage = new MongoStorage(mongoHost, mongoPort)
    val dataPointOutput = new MongoDataPointOutput(mongoStorage)

    val shutDownLatch = new CountDownLatch(1)
    sys.ShutdownHookThread {
      println("shutting down...")
      shutDownLatch.countDown()
    }

    val collector = buildCollector(dataPointOutput)

    collector.start()
    shutDownLatch.await()
    collector.stop()
  }

  private def buildPoller(xmlConfigDir: String): MongoDataPointOutput => CollectorLifecycle = dataPointOutput => {
    val retrieverLifecycle = new JmxRetrieverLifecycle(
      new DataCollectorConfigReader,
      Executors.newSingleThreadScheduledExecutor)

    new StatsOTron(xmlConfigDir, retrieverLifecycle, dataPointOutput, 10, SECONDS)
  }

  private def buildListener(listenerPort: Int): MongoDataPointOutput => CollectorLifecycle = dataPointOutput =>  {
    new ListenOTron(dataPointOutput, listenerPort)
  }
}

