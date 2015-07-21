package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;
import org.jetbrains.annotations.NotNull;

public final class ToxGroupNewException extends ToxException<ToxGroupNewException.Code> {

  public enum Code {
    /**
     * The group failed to announce to the DHT. This indicates a network related error.
     */
    ANNOUNCE,
    /**
     * groupName is null or length is zero.
     */
    EMPTY,
    /**
     * The group instance failed to initialize.
     */
    INIT,
    /**
     * ToxGroupPrivacyState is an invalid type.
     */
    PRIVACY,
    /**
     * The group state failed to initialize. This usually indicates that something went wrongrelated to cryptographic signing.
     */
    STATE,
    /**
     * The group name exceeded {@link ToxCoreConstants#GROUP_MAX_GROUP_NAME_LENGTH}.
     */
    TOO_LONG,
  }

  public ToxGroupNewException(@NotNull Code code) {
    this(code, "");
  }

  public ToxGroupNewException(@NotNull Code code, String message) {
    super(code, message);
  }

}
