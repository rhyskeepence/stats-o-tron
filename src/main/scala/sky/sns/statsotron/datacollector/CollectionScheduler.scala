package sky.sns.statsotron.datacollector

import java.io.File
import akka.actor.Actor._
import akka.actor.{Scheduler, Actor}
import java.util.concurrent.TimeUnit
import sky.sns.statsotron.datacollector.jmx.{RetrieverLifecycle, CollectFrom, CollectionActor}
import sky.sns.statsotron.output.DataPointOutput

class CollectionScheduler(retrieverLifecycle: RetrieverLifecycle, dataPointOutput: DataPointOutput, delay: Long, timeUnit: TimeUnit) extends Actor {

  def receive = {
    case schedule: Schedule =>
      val retrievers = retrieverLifecycle.createRetrievers(schedule.config)
      val dataCollector = actorOf(new CollectionActor(dataPointOutput, schedule.environment)).start()

      Scheduler.schedule(
        dataCollector,
        CollectFrom(retrievers),
        delay,
        delay,
        timeUnit)
  }

  override def postStop() {
    Scheduler.shutdown()
    retrieverLifecycle.shutdown()
  }
}

case class Schedule(config: File) {
  def environment = config.getName.split(".xml").take(1).mkString
}
