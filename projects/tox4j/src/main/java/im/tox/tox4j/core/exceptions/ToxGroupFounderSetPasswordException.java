package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupFounderSetPasswordException extends ToxException<ToxGroupFounderSetPasswordException.Code> {

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
     * The caller does not have the required permissions to set the password.
     */
    PERMISSIONS,
    /**
     * Password length exceeded {@link ToxCoreConstants#GROUP_MAX_PASSWORD_SIZE}.
     */
    TOO_LONG,
  }

  public ToxGroupFounderSetPasswordException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupFounderSetPasswordException(@NotNull Code code, String message) {
    super(code, message);
  }

}
