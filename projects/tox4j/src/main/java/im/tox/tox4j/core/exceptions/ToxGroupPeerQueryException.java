package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupPeerQueryException extends ToxException<ToxGroupPeerQueryException.Code> {

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

  public ToxGroupPeerQueryException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupPeerQueryException(@NotNull Code code, String message) {
    super(code, message);
  }

}
