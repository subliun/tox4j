package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxUserStatus
import org.jetbrains.annotations.NotNull

/*
 * This event is triggered when a peer changes their nickname.
 */
trait GroupPeerStatusCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the status change is intended for.
   * @param peerNumber The peernumber of the peer who has changed their status.
   * @param status The new status of the peer.
   */
  def groupPeerStatus(
    groupNumber: Int, peerNumber: Int, status: ToxUserStatus
  )(state: ToxCoreState): ToxCoreState = state
}
