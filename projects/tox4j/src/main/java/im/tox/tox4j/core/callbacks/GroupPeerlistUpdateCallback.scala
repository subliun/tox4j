package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This callback is triggered when a peer joins or leaves the group, and should be used to
 * retrieve up to date information about the peer list for the client.
 */
trait GroupPeerlistUpdateCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group that must have its peer list updated.
   */
  def groupPeerlistUpdate(
    groupNumber: Int
  )(state: ToxCoreState): ToxCoreState = state
}
