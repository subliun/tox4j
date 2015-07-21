package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupFounderSetPrivacyStateException extends ToxException<ToxGroupFounderSetPrivacyStateException.Code> {

  public enum Code {
    /**
     * The packet failed to send.
     */
    FAIL_SEND,
    /**
     * The privacy state could not be set. This may occur due to an error related tocryptographic signing of the new shared state.
     */
    FAIL_SET,
    /**
     * The group number passed did not designate a valid group.
     */
    GROUP_NOT_FOUND,
    /**
     * ToxGroupPrivacyState is an invalid type.
     */
    INVALID,
    /**
     * The caller does not have the required permissions to set the privacy state.
     */
    PERMISSIONS,
  }

  public ToxGroupFounderSetPrivacyStateException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupFounderSetPrivacyStateException(@NotNull Code code, String message) {
    super(code, message);
  }

}
