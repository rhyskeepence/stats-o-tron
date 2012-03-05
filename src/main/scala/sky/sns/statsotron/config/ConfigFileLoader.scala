package sky.sns.statsotron.config

import xml.{Elem, XML}
import scala.io.Source

class ConfigFileLoader {
  def loadFrom(source: Source) = {
    val collection = XML.loadString(source.mkString)

    new NogginConfiguration(
      (collection \ "jmxConnection" \ "@host").text,
      (collection \ "jmxConnection" \ "@port").text.toInt,
      (collection \ "jmxConnection" \ "auth" \ "@username").text,
      (collection \ "jmxConnection" \ "auth" \ "@password").text,
      createRetrieversFrom(collection)
    )
  }

  def createRetrieversFrom(collection: Elem): Seq[Retriever] = {
    (collection \\ "retriever").map { (retriever) =>
      val retrieverType = (retriever \ "@type").text
      val csvPrefix = (retriever \ "@csvPrefix").text
      val remote = (retriever \ "@remote").text.toBoolean

      retrieverType match {
        case "MEMORY" => new MemoryRetriever(csvPrefix, remote)
        case "CPU" => new CpuRetriever(csvPrefix, remote)
        case "THREAD" => new JvmThreadsRetriever(csvPrefix, remote)
        case "DATABASE_POOL" => new DatabasePoolRetriever(csvPrefix, remote, (retriever \ "config").text)
        case "WEB_SESSION" => new HttpSessionRetriever(csvPrefix, remote, (retriever \ "config").text)
        case "THREAD_POOL" => new ThreadPoolRetriever(csvPrefix, remote, (retriever \ "config").text)
        case "QUEUE" => new QueueRetriever(csvPrefix, remote, (retriever \ "brokerName").text, (retriever \ "queueNames").text)
        case "CUSTOM" => new CustomRetriever(csvPrefix, remote, (retriever \ "mbeanName").text, (retriever \ "attributeName").text, (retriever \ "csvFieldName").text)
        case "CUSTOM_BY_ATTRIBUTE" => new CustomByAttributeRetriever(csvPrefix, remote, (retriever \ "classQueryString").text, (retriever \ "attribute").text, (retriever \ "attributeName").text, (retriever \ "attributeValue").text, (retriever \ "csvFieldName").text)
        case "HIBERNATE_SESSION" => new HibernateSessionRetriever(csvPrefix, remote, (retriever \ "applicationName").text)
      }
    }
  }
}

case class NogginConfiguration(host: String,
                               port: Int,
                               username: String,
                               password: String,
                               retrievers: Seq[Retriever])

trait Retriever

case class MemoryRetriever(csvPrefix: String, remote: Boolean) extends Retriever

case class CpuRetriever(csvPrefix: String, remote: Boolean) extends Retriever

case class JvmThreadsRetriever(csvPrefix: String, remote: Boolean) extends Retriever

case class DatabasePoolRetriever(csvPrefix: String, remote: Boolean, username: String) extends Retriever

case class HttpSessionRetriever(csvPrefix: String, remote: Boolean, applicationName: String) extends Retriever

case class ThreadPoolRetriever(csvPrefix: String, remote: Boolean, connectorPort: String) extends Retriever

case class QueueRetriever(csvPrefix: String,
                          remote: Boolean,
                          brokerName: String,
                          queueNames: String) extends Retriever

case class CustomRetriever(csvPrefix: String,
                           remote: Boolean,
                           mbeanName: String,
                           attributeName: String,
                           csvFieldName: String) extends Retriever

case class CustomByAttributeRetriever(csvPrefix: String,
                                      remote: Boolean,
                                      classQueryString: String,
                                      attribute: String,
                                      attributeName: String,
                                      attributeValue: String,
                                      csvFieldName: String) extends Retriever

case class HibernateSessionRetriever(csvPrefix: String,
                                     remote: Boolean,
                                     applicationName: String) extends Retriever