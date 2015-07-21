package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupModRemovePeerException extends ToxException<ToxGroupModRemovePeerException.Code> {

  public enum Code {
    /**
     * The peer failed to be removed from the group. If a ban was set, this error indicatesthat the ban entry could not be created. This may either be due to the entry containinginvalid peer information, or a failure to cryptographically authenticate the entry.
     */
    FAIL_ACTION,
    /**
     * The packet failed to send.
     */
    FAIL_SEND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The peer number passed did not designate a valid peer.
     */
    PEER_NOT_FOUND,
    /**
     * The caller does not have the required permissions for this action.
     */
    PERMISSIONS,
  }

  public ToxGroupModRemovePeerException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupModRemovePeerException(@NotNull Code code, String message) {
    super(code, message);
  }

}
