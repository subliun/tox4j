package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupInviteFriendException extends ToxException<ToxGroupInviteFriendException.Code> {

  public enum Code {
    /**
     * Packet failed to send.
     */
    FAIL_SEND,
    /**
     * The friend number passed did not designate a valid friend.
     */
    FRIEND_NOT_FOUND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * Creation of the invite packet failed. This indicates a network related error.
     */
    INVITE_FAIL,
  }

  public ToxGroupInviteFriendException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupInviteFriendException(@NotNull Code code, String message) {
    super(code, message);
  }

}
