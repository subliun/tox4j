package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupBanQueryException extends ToxException<ToxGroupBanQueryException.Code> {

  public enum Code {
    /**
     * The ban ID does not designate a valid ban list entry.
     */
    BAD_ID,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
  }

  public ToxGroupBanQueryException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupBanQueryException(@NotNull Code code, String message) {
    super(code, message);
  }

}
