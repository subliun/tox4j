package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when the client has successfully joined a group. Use this to initialize
 * any group information the client may need.
 */
trait GroupSelfJoinCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group that the client has joined.
   */
  def groupSelfJoin(
    groupNumber: Int
  )(state: ToxCoreState): ToxCoreState = state
}
