package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupModRemoveBanException extends ToxException<ToxGroupModRemoveBanException.Code> {

  public enum Code {
    /**
     * The ban entry could not be removed. This may occur if ban_id does not designatea valid ban entry.
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
     * The caller does not have the required permissions for this action.
     */
    PERMISSIONS,
  }

  public ToxGroupModRemoveBanException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupModRemoveBanException(@NotNull Code code, String message) {
    super(code, message);
  }

}
