package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when the group founder changes the group password.
 */
trait GroupPasswordCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group for which the password has changed.
   * @param password The new group password.
   */
  def groupPassword(
    groupNumber: Int, password: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
