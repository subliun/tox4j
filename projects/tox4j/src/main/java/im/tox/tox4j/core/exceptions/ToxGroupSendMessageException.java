package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupSendMessageException extends ToxException<ToxGroupSendMessageException.Code> {

  public enum Code {
    /**
     * The message type is invalid.
     */
    BAD_TYPE,
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
     * The caller does not have the required permissions to send group messages.
     */
    PERMISSIONS,
    /**
     * Message length exceeded {@link ToxCoreConstants#MAX_MESSAGE_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupSendMessageException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupSendMessageException(@NotNull Code code, String message) {
    super(code, message);
  }

}
