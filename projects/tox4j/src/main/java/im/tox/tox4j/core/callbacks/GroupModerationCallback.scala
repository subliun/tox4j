package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxGroupModEvent
import org.jetbrains.annotations.NotNull

/*
* This event is triggered when a moderator or founder executes a moderation event.
*/
trait GroupModerationCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the event is intended for.
   * @param sourcePeerNumber The peernumber of the peer who initiated the event.
   * @param targetPeerNumber The peernumber of the peer who is the target of the event.
   * @param groupModEventType The type of event (one of [[ToxGroupModEvent]]).
   */
  def groupModeration(
    groupNumber: Int, sourcePeerNumber: Int, targetPeerNumber: Int, groupModEventType: ToxGroupModEvent
  )(state: ToxCoreState): ToxCoreState = state
}
