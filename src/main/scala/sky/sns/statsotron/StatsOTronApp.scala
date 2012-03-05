package sky.sns.statsotron

import sky.sns.statsotron.datacollector.jmx.JmxRetrieverLifecycle
import org.noggin.config.xml.DataCollectorConfigReader
import java.util.concurrent.TimeUnit._
import java.util.concurrent.{CountDownLatch, Executors}
import sky.sns.statsotron.output.mongo.{MongoStorage, MongoDataPointOutput}

object StatsOTronApp extends App {

  val usage = "Usage: statsotron [path-to-xml-config-dir]"

  args.toList match {
    case xmlConfigDir :: tail =>
      startStatsOTron(xmlConfigDir)

    case _ =>
      println(usage)
  }

  private def startStatsOTron(xmlConfigDir: String) {
    val retrieverLifecycle = new JmxRetrieverLifecycle(
      new DataCollectorConfigReader,
      Executors.newSingleThreadScheduledExecutor)
    val mongoStorage = new MongoStorage
    val dataPointOutput = new MongoDataPointOutput(mongoStorage)

    val statsOTron = new StatsOTron(xmlConfigDir, retrieverLifecycle, dataPointOutput, 10, SECONDS)
    val shutDownLatch = new CountDownLatch(1)

    sys.ShutdownHookThread {
      println("shutting down...")
      shutDownLatch.countDown()
    }

    statsOTron.start()
    shutDownLatch.await()
    statsOTron.stop()

    mongoStorage.close()
  }
}

