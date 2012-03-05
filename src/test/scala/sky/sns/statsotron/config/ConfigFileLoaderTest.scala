package sky.sns.statsotron.config

import org.specs.SpecificationWithJUnit
import scala.io.Source

class ConfigFileLoaderTest extends SpecificationWithJUnit {

  val nogginConfiguration = new ConfigFileLoader().loadFrom(Source.fromString(configWithAllRetrievers))

  "The Config File Loader" should {

    "load the correct jmx host " in {
      nogginConfiguration.host mustEqual "test2.is.uk.easynet.net"
    }

    "load the correct jmx port" in {
      nogginConfiguration.port mustEqual 11129
    }

    "should load the correct jmx username" in {
      nogginConfiguration.username mustEqual "readonlyuser"
    }

    "should load the correct jmx password" in {
      nogginConfiguration.password mustEqual "somepwd"
    }

    "should have a Memory retriever" in {
      nogginConfiguration.retrievers must contain(new MemoryRetriever("jvm", true))
    }

    "should have a CPU retriever" in {
      nogginConfiguration.retrievers must contain(new CpuRetriever("jvm", true))
    }

    "should have a Thread retriever" in {
      nogginConfiguration.retrievers must contain(new JvmThreadsRetriever("jvm", true))
    }

    "should have a Database Pool retriever" in {
      nogginConfiguration.retrievers must contain(new DatabasePoolRetriever("database", true, "bandaid_user"))
    }

    "should have a Web Session retriever" in {
      nogginConfiguration.retrievers must contain(new HttpSessionRetriever("catalina", true, "bandaid"))
    }

    "should have a Thread Pool retriever" in {
      nogginConfiguration.retrievers must contain(new ThreadPoolRetriever("catalinaHttp", true, "http-11125"))
    }
  }

  def configWithAllRetrievers = """
    <collection xmlns="http://org.noggin/data-collection/1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <jmxConnection host="test2.is.uk.easynet.net" port="11129">
            <auth username="readonlyuser" password="somepwd"/>
        </jmxConnection>
        <retriever type="MEMORY" csvPrefix="jvm" remote="true"/>
        <retriever type="CPU" csvPrefix="jvm" remote="true"/>
        <retriever type="THREAD" csvPrefix="jvm" remote="true"/>
        <retriever type="DATABASE_POOL" csvPrefix="database" remote="true">
            <config name="databaseUser">bandaid_user</config>
        </retriever>
        <retriever type="WEB_SESSION" csvPrefix="catalina" remote="true">
            <config name="applicationName">bandaid</config>
        </retriever>
        <retriever type="THREAD_POOL" csvPrefix="catalinaHttp" remote="true">
            <config name="connectorPort">http-11125</config>
        </retriever>
    </collection>
  """
}
