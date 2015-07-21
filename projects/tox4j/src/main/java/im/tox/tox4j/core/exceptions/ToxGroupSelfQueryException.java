package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupSelfQueryException extends ToxException<ToxGroupSelfQueryException.Code> {

  public enum Code {
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
  }

  public ToxGroupSelfQueryException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupSelfQueryException(@NotNull Code code, String message) {
    super(code, message);
  }

}
