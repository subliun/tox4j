package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupLeaveException extends ToxException<ToxGroupLeaveException.Code> {

  public enum Code {
    /**
     * The group chat instance failed to be deleted. This may occur due to memory related errors.
     */
    DELETE_FAIL,
    /**
     * The parting packet failed to send.
     */
    FAIL_SEND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * Message length exceeded {@link ToxCoreConstants#GROUP_MAX_PART_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupLeaveException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupLeaveException(@NotNull Code code, String message) {
    super(code, message);
  }

}
