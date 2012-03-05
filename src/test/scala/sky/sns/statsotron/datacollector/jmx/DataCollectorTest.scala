package sky.sns.statsotron.datacollector.jmx

import org.specs.SpecificationWithJUnit
import org.noggin.instrumentation.retriever.{RetrievedData, Retriever}

class DataCollectorTest extends SpecificationWithJUnit {

  val time = 1234
  val prefix = "prefix"
  val environment = "environment"

  val collector = new DataCollector(environment)

  "Should call the retriever and collect a DataPoint" in {

    val retriever = stubRetriever(time) {
      data =>
        data.addRecord("header-1", 5)
        data.addRecord("header-2", 10)
    }

    val dataPoint = collector.collectFrom(List(retriever))

    dataPoint.timestamp mustEqual time
    dataPoint.environment mustEqual environment
    dataPoint.metrics.size mustEqual 2

    dataPoint.metrics(0).name mustEqual "prefixheader-1"
    dataPoint.metrics(0).value mustEqual 5

    dataPoint.metrics(1).name mustEqual "prefixheader-2"
    dataPoint.metrics(1).value mustEqual 10
  }

  def stubRetriever(timeInMillis: Long)(setupRetrievedData: RetrievedData => Unit) = {
    new Retriever(null, null) {
      def retrieveData() = {
        val data = new RetrievedData(prefix) {
          override def getTime = timeInMillis
        }
        setupRetrievedData(data)
        data
      }

      def headers() = null
    }
  }


}
