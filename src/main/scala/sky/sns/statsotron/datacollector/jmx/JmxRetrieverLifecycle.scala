package sky.sns.statsotron.datacollector.jmx

import org.noggin.config.xml.DataCollectorConfigReader
import org.noggin.instrumentation.ConnectionDetails
import com.google.code.tempusfugit.temporal.Duration._
import java.io.{File, FileReader}
import scala.collection.JavaConversions._
import org.noggin.instrumentation.retriever.{Retriever, RetrieverFactory}
import scala.collection.mutable.SynchronizedQueue
import org.noggin.instrumentation.jmx.{JmxConnectionManager, DefaultJmxConnectionManager, DefaultJmxConnectionFactory}
import java.util.concurrent.ScheduledExecutorService

class JmxRetrieverLifecycle(configReader: DataCollectorConfigReader, reconnectingScheduler: ScheduledExecutorService) extends RetrieverLifecycle {

  var connectionManagers = new SynchronizedQueue[JmxConnectionManager]

  def createRetrievers(config: File): Seq[Retriever] = {
    val connectionDetails: ConnectionDetails = configReader.getConnectionDetails(newFileReader(config))
    val retrieverConfigurations = configReader.getRetrieverConfig(newFileReader(config))

    val connectionManager = new DefaultJmxConnectionManager(new DefaultJmxConnectionFactory, reconnectingScheduler, minutes(5), connectionDetails)
    connectionManagers += connectionManager

    val retrieverFactory = new RetrieverFactory(connectionManager)
    retrieverConfigurations.map(retrieverFactory.createRetriever(_))
  }

  def shutdown() {
    connectionManagers.foreach { _.shutdown() }
  }

  private def newFileReader(config: File): FileReader = new FileReader(config)

}