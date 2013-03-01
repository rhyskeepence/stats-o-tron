package statsotron.datacollector.udp

import java.net.DatagramPacket
import scala.io.Source
import akka.actor.Actor
import statsotron.output.DataPointOutput
import statsotron.model.{Metric, DataPoint}

class UdpConnectionHandler(dataPointOutput: DataPointOutput) extends Actor {
  val inputPattern = """([\w\-]+):([\w\-]+)=(\d+)""".r

  def receive = {
    case IncomingConnection(packet) =>
      Source.fromBytes(packet.getData).getLines().map(_.trim).foreach { input =>

        println("Incoming connection from " + packet.getAddress + " with data " + input)

        input match {
          case inputPattern(environment, metricName, value) =>
            dataPointOutput.write(
              DataPoint(System.currentTimeMillis(), environment,
                List(
                  Metric(metricName, value.toDouble)
                )
              )
            )

          case _ =>
            println("Received invalid input: [%s] - must be in the format '[environment]:[metric-name]=[numeric-value]'".format(input))
        }
      }
  }
}

case class IncomingConnection(packet: DatagramPacket)
