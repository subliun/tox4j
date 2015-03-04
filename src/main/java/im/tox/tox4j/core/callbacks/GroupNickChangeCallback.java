package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupNickChangeCallback {

    void groupNickChange(int groupNumber, int peerNumber, @NotNull byte[] nick);

}
