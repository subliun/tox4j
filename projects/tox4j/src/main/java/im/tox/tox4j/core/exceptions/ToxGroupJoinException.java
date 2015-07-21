package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupJoinException extends ToxException<ToxGroupJoinException.Code> {

  public enum Code {
    /**
     * The chat_id is null.
     */
    BAD_CHAT_ID,
    /**
     * The group instance failed to initialize.
     */
    INIT,
    /**
     * Password length exceeded {@link ToxCoreConstants#GROUP_MAX_PASSWORD_SIZE}.
     */
    TOO_LONG,
  }

  public ToxGroupJoinException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupJoinException(@NotNull Code code, String message) {
    super(code, message);
  }

}
