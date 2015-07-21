package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when you receive a group message.
 */
trait GroupPrivateMessageCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the private message is intended for.
   * @param peerNumber The peernumber of the peer who sent the private message.
   * @param message The message data.
   */
  def groupPrivateMessage(
    groupNumber: Int, peerNumber: Int, message: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
