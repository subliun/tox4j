package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupToggleIgnoreException extends ToxException<ToxGroupToggleIgnoreException.Code> {

  public enum Code {
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The peer number passed did not designate a valid peer.
     */
    PEER_NOT_FOUND,
  }

  public ToxGroupToggleIgnoreException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupToggleIgnoreException(@NotNull Code code, String message) {
    super(code, message);
  }

}
