package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupTopicSetException extends ToxException<ToxGroupTopicSetException.Code> {

  public enum Code {
    /**
     * The packet failed to send.
     */
    FAIL_SEND,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * The caller does not have the required permissions to set the topic.
     */
    PERMISSIONS,
    /**
     * Topic length exceeded {@link ToxCoreConstants#GROUP_MAX_TOPIC_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupTopicSetException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupTopicSetException(@NotNull Code code, String message) {
    super(code, message);
  }

}
