package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupSelfNameSetException extends ToxException<ToxGroupSelfNameSetException.Code> {

  public enum Code {
    /**
     * The packet failed to send.
     */
    FAIL_SEND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The length given to the set function is zero or name is null
     */
    INVALID,
    /**
     * The name is already taken by another peer in the group.
     */
    TAKEN,
    /**
     * Name length exceeded {@link ToxCoreConstants#MAX_NAME_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupSelfNameSetException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupSelfNameSetException(@NotNull Code code, String message) {
    super(code, message);
  }

}
