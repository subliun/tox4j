package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when the group founder changes the privacy state.
 */
trait GroupPrivacyStateCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the topic change is intended for.
   * @param privacyState The new privacy state.
   */
  def groupPrivacyState(
    groupNumber: Int, privacyState: ToxGroupPrivacyState
  )(state: ToxCoreState): ToxCoreState = state
}
