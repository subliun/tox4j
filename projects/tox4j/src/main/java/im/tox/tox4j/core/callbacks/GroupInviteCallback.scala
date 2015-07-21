package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when you receive a group invite from a friend.
 */
trait GroupInviteCallback[ToxCoreState] {
  /**
   * @param friendNumber The friendnumber of the contact who invited you.
   * @param inviteData The invite data.
   */
  def groupInvite(
    friendNumber: Int, inviteData: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
