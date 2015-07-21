package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a peer exits the group. Do not use this to update the peer list; use
 * [[GroupPeerlistUpdateCallback]] instead.
 */
trait GroupPeerExitCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group in which a peer has left.
   * @param peerNumber The peernumber of the peer who left the group.
   * @param partMessage The parting message data.
   */
  def groupPeerExit(
    groupNumber: Int, peerNumber: Int, partMessage: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
