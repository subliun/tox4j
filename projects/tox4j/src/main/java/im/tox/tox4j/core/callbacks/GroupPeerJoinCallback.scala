package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a peer joins the group. Do not use this to update the peer list; use
 * [[GroupPeerlistUpdateCallback]] instead.
 */
trait GroupPeerJoinCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group in which a new peer has joined.
   * @param peerNumber The peernumber of the new peer.
   */
  def groupPeerJoin(
    groupNumber: Int, peerNumber: Int
  )(state: ToxCoreState): ToxCoreState = state
}
