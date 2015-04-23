package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.exceptions.ToxException;

public final class ToxGroupAcceptInviteException extends ToxException {
    public static enum Code {
        FAILED,
    }

    private final @NotNull
    Code code;

    public ToxGroupAcceptInviteException(@NotNull Code code) {
        this.code = code;
    }

    @NotNull
    @Override
    public Code getCode() {
        return code;
    }
}
