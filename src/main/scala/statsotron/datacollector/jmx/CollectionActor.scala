package statsotron.datacollector.jmx

import akka.actor.Actor
import org.noggin.instrumentation.retriever.Retriever
import statsotron.output.DataPointOutput

class CollectionActor(dataPointOutput: DataPointOutput, collectionName: String) extends Actor {
  val dataCollector = new DataCollector(collectionName)

  def receive = {
    case CollectFrom(retrievers) =>
      dataPointOutput.write(dataCollector.collectFrom(retrievers))
  }
}

case class CollectFrom(retrievers: Seq[Retriever])
