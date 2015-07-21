package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a peer changes their nickname.
 */
trait GroupPeerNameCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the name change is intended for.
   * @param peerNumber The peernumber of the peer who has changed their name.
   * @param name The name data.
   */
  def groupPeerName(
    groupNumber: Int, peerNumber: Int, name: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
