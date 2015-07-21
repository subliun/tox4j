package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupReconnectException extends ToxException<ToxGroupReconnectException.Code> {

  public enum Code {
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
  }

  public ToxGroupReconnectException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupReconnectException(@NotNull Code code, String message) {
    super(code, message);
  }

}
