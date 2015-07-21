package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when the group founder changes the maximum peer limit.
 */
trait GroupPeerLimitCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group for which the peer limit has changed.
   * @param peerLimit The new peer limit for the group.
   */
  def groupPeerLimit(
    groupNumber: Int, peerLimit: Int
  )(state: ToxCoreState): ToxCoreState = state
}
