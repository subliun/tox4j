package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupFounderSetPeerLimitException extends ToxException<ToxGroupFounderSetPeerLimitException.Code> {

  public enum Code {
    /**
     * The packet failed to send.
     */
    FAIL_SEND,
    /**
     * The peer limit could not be set. This may occur due to an error related tocryptographic signing of the new shared state.
     */
    FAIL_SET,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The caller does not have the required permissions to set the peer limit.
     */
    PERMISSIONS,
  }

  public ToxGroupFounderSetPeerLimitException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupFounderSetPeerLimitException(@NotNull Code code, String message) {
    super(code, message);
  }

}
