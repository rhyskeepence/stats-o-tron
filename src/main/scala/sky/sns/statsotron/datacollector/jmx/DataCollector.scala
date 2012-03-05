package sky.sns.statsotron.datacollector.jmx

import org.noggin.instrumentation.retriever.{RetrieverDataCollector, Retriever}
import scala.collection.JavaConversions._
import sky.sns.statsotron.model.{DataPoint, Metric}

class DataCollector(environment: String) {

  def collectFrom(retrievers: Seq[Retriever]) = {
    val retrievedData = retrieve(retrievers)
    val headersAndData = retrievedData.flatMap {data => data.getHeaders.zip(data.getData)}
    val timestamp = retrievedData.headOption.map(_.getTime.longValue()).getOrElse(System.currentTimeMillis())

    val metrics = headersAndData.map {
      case (name, value) =>
        Metric(name, value.toDouble)
    }

    DataPoint(timestamp, environment, metrics.toList)
  }

  private def retrieve(retrievers: Seq[Retriever]) = {
    new RetrieverDataCollector(retrievers).collect()
  }

}
