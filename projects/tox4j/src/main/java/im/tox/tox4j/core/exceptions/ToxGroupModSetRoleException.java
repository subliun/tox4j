package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupModSetRoleException extends ToxException<ToxGroupModSetRoleException.Code> {

  public enum Code {
    /**
     * The role assignment is invalid. This will occur if you try to set a peer's role tothe role they already have.
     */
    ASSIGNMENT,
    /**
     * The role was not successfully set. This may occur if something goes wrong with role setting,or if the packet fails to send.
     */
    FAIL_ACTION,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The peer number passed did not designate a valid peer. Note: you cannot set your own role.
     */
    PEER_NOT_FOUND,
    /**
     * The caller does not have the required permissions for this action.
     */
    PERMISSIONS,
  }

  public ToxGroupModSetRoleException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupModSetRoleException(@NotNull Code code, String message) {
    super(code, message);
  }

}
