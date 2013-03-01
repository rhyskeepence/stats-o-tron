package statsotron.datacollector.udp

import java.util.concurrent.atomic.AtomicBoolean
import java.io.IOException
import akka.actor._
import akka.actor.Actor._
import java.net._
import statsotron.output.DataPointOutput

case object Start
case object Stop

class UdpListener(dataPointOutput: DataPointOutput, port: Int) extends Actor {

  var shouldListen = new AtomicBoolean(true)

  def receive = {
    case Start =>
      val listener = new DatagramSocket(port)
      listener.setSoTimeout(1000)

      val connectionHandler = actorOf(new UdpConnectionHandler(dataPointOutput)).start()

      println("Listening for UDP connections on port %d".format(port))

      while (shouldListen.get) {
        try {
          val buffer = Array.ofDim[Byte](1024)
          val receivedPacket = new DatagramPacket(buffer, buffer.length)

          listener.receive(receivedPacket)
          connectionHandler ! IncomingConnection(receivedPacket)

        } catch {
          case timeout: SocketTimeoutException => // expected, will just loop
          case ioe: IOException => ioe.printStackTrace()
        }
      }

      listener.close()

    case Stop =>
      shouldListen set false
  }

}

