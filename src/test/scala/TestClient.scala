import com.typesafe.scalalogging.Logger
import im.tox.tox4j.ToxCoreImpl
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.{ToxConstants, ToxOptions}
import org.slf4j.LoggerFactory

object TestClient extends App {

  private val logger = Logger(LoggerFactory.getLogger(TestClient.getClass))


  private def parseClientId(id: String): Array[Byte] = {
    val clientId = Array.ofDim[Byte](ToxConstants.CLIENT_ID_SIZE)

    for (i <- 0 until ToxConstants.CLIENT_ID_SIZE) {
      clientId(i) = (
        (fromHexDigit(id.charAt(i * 2)) << 4) +
          fromHexDigit(id.charAt(i * 2 + 1))
        ).toByte
    }
    clientId
  }

  private def fromHexDigit(c: Char): Byte = {
    if (c >= '0' && c <= '9') {
      (c - '0').toByte
    } else if (c >= 'a' && c <= 'f') {
      (c - 'A' + 10).toByte
    } else if (c >= 'A' && c <= 'F') {
      (c - 'A' + 10).toByte
    } else {
      throw new IllegalArgumentException("Non-hex digit character: " + c)
    }
  }

  (args match {
    case Array("--bootstrap", host, port, key, count) =>
      (Some(host, Integer.parseInt(port), key), Integer.parseInt(count))
    case Array("--bootstrap", host, port, key) =>
      (Some(host, Integer.parseInt(port), key), 1)
    case Array("--bootstrap", count) =>
      (Some("144.76.60.215", 33445, "04119E835DF3E78BACF0F84235B300546AF8B936F035185E2A8E9E0A67C8924F"), Integer.parseInt(count))
    case Array(count) =>
      (None, Integer.parseInt(count))
    case _ =>
      (None, 1)
  }) match {
    case (bootstrap, count) =>
      logger.info(s"Creating $count toxes")

      val toxes = (1 to count) map { id =>
        val tox = new ToxCoreImpl({
          val options = new ToxOptions
          options.setIpv6Enabled(true)
          options.setUdpEnabled(bootstrap.isEmpty)
          options
        })

        tox.callback(new TestEventListener(id))

        bootstrap match {
          case Some((host, port, key)) =>
            logger.info(s"[$id] Bootstrapping to $host:$port")
            tox.bootstrap(host, port, parseClientId(key))
          case None =>
        }
        tox
      }

      logger.info("Starting event loop")
      while (true) {
        toxes.foreach(_.iteration)
        Thread.sleep(toxes.map(_.iterationInterval).max)
      }
  }

  private sealed class TestEventListener(id: Int) extends ToxEventListener {

    override def friendStatus(friendNumber: Int, status: ToxStatus): Unit = {
      logger.info(s"[$id] friendStatus($friendNumber, $status)")
    }

    override def friendTyping(friendNumber: Int, isTyping: Boolean): Unit = {
      logger.info(s"[$id] friendTyping($friendNumber, $isTyping)")
    }

    override def connectionStatus(connectionStatus: ToxConnection): Unit = {
      logger.info(s"[$id] connectionStatus($connectionStatus)")
    }

    override def friendName(friendNumber: Int, name: Array[Byte]): Unit = {
      logger.info(s"[$id] friendName($friendNumber, ${new String(name)})")
    }

    override def friendAction(friendNumber: Int, timeDelta: Int, message: Array[Byte]): Unit = {
      logger.info(s"[$id] friendAction($friendNumber, $timeDelta, ${new String(message)})")
    }

    override def friendMessage(friendNumber: Int, timeDelta: Int, message: Array[Byte]): Unit = {
      logger.info(s"[$id] friendMessage($friendNumber, $timeDelta, ${new String(message)})")
    }

    override def friendLossyPacket(friendNumber: Int, data: Array[Byte]): Unit = {
      logger.info(s"[$id] friendLossyPacket($friendNumber, ${new String(data)})")
    }

    override def fileReceive(friendNumber: Int, fileNumber: Int, kind: ToxFileKind, fileSize: Long, filename: Array[Byte]): Unit = {
      logger.info(s"[$id] fileReceive($friendNumber, $fileNumber, $kind, $fileSize, ${new String(filename)}})")
    }

    override def friendRequest(clientId: Array[Byte], timeDelta: Int, message: Array[Byte]): Unit = {
      logger.info(s"[$id] friendRequest($clientId, $timeDelta, ${new String(message)})")
    }

    override def fileRequestChunk(friendNumber: Int, fileNumber: Int, position: Long, length: Int): Unit = {
      logger.info(s"[$id] fileRequestChunk($friendNumber, $fileNumber, $position, $length)")
    }

    override def fileReceiveChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]): Unit = {
      logger.info(s"[$id] fileReceiveChunk($friendNumber, $fileNumber, $position, ${new String(data)})")
    }

    override def friendLosslessPacket(friendNumber: Int, data: Array[Byte]): Unit = {
      logger.info(s"[$id] friendLosslessPacket($friendNumber, ${new String(data)})")
    }

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection): Unit = {
      logger.info(s"[$id] friendConnectionStatus($friendNumber, $connectionStatus)")
    }

    override def fileControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl): Unit = {
      logger.info(s"[$id] fileControl($friendNumber, $fileNumber, $control)")
    }

    override def friendStatusMessage(friendNumber: Int, message: Array[Byte]): Unit = {
      logger.info(s"[$id] friendStatusMessage($friendNumber, ${new String(message)})")
    }

    override def readReceipt(friendNumber: Int, messageId: Int): Unit = {
      logger.info(s"[$id] readReceipt($friendNumber, $messageId)")
    }

    override def groupMessage(groupNumber: Int, peerNumber: Int, timeDelta: Int, message: Array[Byte]): Unit = {
      logger.info(s"[$id] groupMessage($groupNumber, $peerNumber, $timeDelta, ${new String(message)})")
    }

    override def groupSelfJoin(groupNumber: Int): Unit = ???

    override def groupNickChange(groupNumber: Int, peerNumber: Int, nick: Array[Byte]): Unit = ???

    override def groupPeerlistUpdate(groupNumber: Int): Unit = ???

    override def groupPeerExit(groupNumber: Int, peerNumber: Int, partMessage: Array[Byte]): Unit = ???

    override def groupTopicChange(groupNumber: Int, peerNumber: Int, topic: Array[Byte]): Unit = ???

    override def groupSelfTimeout(groupNumber: Int): Unit = ???

    override def groupPrivateMessage(groupNumber: Int, peerNumber: Int, timeDelta: Int, message: Array[Byte]): Unit = ???

    override def groupInvite(friendNumber: Int, inviteData: Array[Byte]): Unit = ???

    override def groupPeerJoin(groupNumber: Int, peerNumber: Int): Unit = ???

    override def groupAction(groupNumber: Int, peerNumber: Int, timeDelta: Int, message: Array[Byte]): Unit = ???

    override def groupJoinRejected(groupNumber: Int, rejectedReason: ToxGroupJoinRejected): Unit = ???
  }

}
