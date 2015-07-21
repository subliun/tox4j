package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxGroupJoinFail
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when the client fails to join a group.
 */
trait GroupJoinFailCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group for which the join has failed.
   * @param groupJoinFailType The type of group rejection.
   */
  def groupJoinFail(
    groupNumber: Int, groupJoinFailType: ToxGroupJoinFail
  )(state: ToxCoreState): ToxCoreState = state
}
