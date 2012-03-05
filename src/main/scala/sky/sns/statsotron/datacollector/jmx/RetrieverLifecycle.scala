package sky.sns.statsotron.datacollector.jmx

import java.io.File
import org.noggin.instrumentation.retriever.Retriever

trait RetrieverLifecycle {
  def createRetrievers(config: File): Seq[Retriever]
  def shutdown()
}
