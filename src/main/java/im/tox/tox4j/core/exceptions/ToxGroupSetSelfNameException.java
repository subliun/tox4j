package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.exceptions.ToxException;

public class ToxGroupSetSelfNameException extends ToxException {
       public static enum Code {
           FAILED,
       }

       private final @NotNull
       Code code;
       public ToxGroupSetSelfNameException(@NotNull Code code) {
           this.code = code;
       }

       @NotNull
       @Override
       public Code getCode() { return code; }
}
