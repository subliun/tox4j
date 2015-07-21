package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupSelfStatusSetException extends ToxException<ToxGroupSelfStatusSetException.Code> {

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
     * An invalid type was passed to the set function.
     */
    INVALID,
  }

  public ToxGroupSelfStatusSetException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupSelfStatusSetException(@NotNull Code code, String message) {
    super(code, message);
  }

}
