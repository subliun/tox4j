package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupInviteAcceptException extends ToxException<ToxGroupInviteAcceptException.Code> {

  public enum Code {
    /**
     * The invite data is not in the expected format.
     */
    BAD_INVITE,
    /**
     * The group instance failed to initialize.
     */
    INIT_FAILED,
    /**
     * Password length exceeded {@link ToxCoreConstants#GROUP_MAX_PASSWORD_SIZE}.
     */
    TOO_LONG,
  }

  public ToxGroupInviteAcceptException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupInviteAcceptException(@NotNull Code code, String message) {
    super(code, message);
  }

}
