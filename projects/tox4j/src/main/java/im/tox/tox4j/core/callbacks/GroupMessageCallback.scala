package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxMessageType
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when you receive a group message.
 */
trait GroupMessageCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the message is intended for.
   * @param peerNumber The peernumber of the peer who sent the message.
   * @param messageType Message type (normal, action, ...).
   * @param message The message data.
   */
  def groupMessage(
    groupNumber: Int, peerNumber: Int, messageType: ToxMessageType, message: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
