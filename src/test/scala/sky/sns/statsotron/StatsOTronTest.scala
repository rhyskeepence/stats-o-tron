package sky.sns.statsotron

import org.specs.SpecificationWithJUnit
import sky.sns.statsotron.datacollector.jmx.RetrieverLifecycle
import java.io.File
import org.noggin.instrumentation.retriever.{RetrievedData, Retriever}
import org.specs.mock.Mockito
import java.util.concurrent.TimeUnit
import sky.sns.statsotron.output.DataPointOutput
import org.specs.util.TimeConversions._
import scala.collection.JavaConversions._
import org.mockito.Matchers._
import sky.sns.statsotron.model.{Metric, DataPoint}

class StatsOTronTest extends SpecificationWithJUnit with Mockito {
  val configurationFile = File.createTempFile("statsotron-sampleconfig", ".xml")
  configurationFile.deleteOnExit()

  val configurationPath = configurationFile.getParent
  
  val retrieverLifecycle = mock[RetrieverLifecycle]
  val output = new StubDataPointOutput
  val statsOtron = new StatsOTron(configurationPath, retrieverLifecycle, output, 50, TimeUnit.MILLISECONDS)

  retrieverLifecycle.createRetrievers(anyObject[File]) returns staticRetriever

  "Should capture metrics until shut down" in {
    statsOtron.start()

    val configurationPrefix = configurationFile.getName.split(".xml").take(1).mkString

    val expectedDataPoint =
      Some(DataPoint(
        123,
        configurationPrefix,
        List(
          Metric("x-value-a", 100.0),
          Metric("x-value-b", 200.0))))

    output.dataWritten.lastOption must eventually (50, 10.milliseconds) {
      beEqualTo(expectedDataPoint)
    }

    statsOtron.stop()
  }

  "Should shutdown the retriever after completion" in {
    statsOtron.start()
    statsOtron.stop()

    there was one(retrieverLifecycle).shutdown()
  }

  def staticRetriever = {
    List(new Retriever(null, null) {
      def retrieveData() = {
        val data = new RetrievedData("x-") {
          override def getTime = 123
        }
        data.addRecord("value-a", 100.0)
        data.addRecord("value-b", 200.0)
        data
      }

      def headers() = List[String]()
    })
  }

  class StubDataPointOutput extends DataPointOutput {
    var dataWritten = List[DataPoint]()

    def write(dataPoint: DataPoint) {
      dataWritten = dataWritten :+ dataPoint
    }
  }
}
