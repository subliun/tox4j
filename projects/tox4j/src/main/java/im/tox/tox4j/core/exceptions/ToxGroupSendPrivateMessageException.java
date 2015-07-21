package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupSendPrivateMessageException extends ToxException<ToxGroupSendPrivateMessageException.Code> {

  public enum Code {
    /**
     * The message length is zero.
     */
    EMPTY,
    /**
     * Packet failed to send.
     */
    FAIL_SEND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The peer number passed did not designate a valid peer.
     */
    PEER_NOT_FOUND,
    /**
     * The caller does not have the required permissions to send group messages.
     */
    PERMISSIONS,
    /**
     * Message length exceeded {@link ToxCoreConstants#MAX_MESSAGE_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupSendPrivateMessageException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupSendPrivateMessageException(@NotNull Code code, String message) {
    super(code, message);
  }

}
