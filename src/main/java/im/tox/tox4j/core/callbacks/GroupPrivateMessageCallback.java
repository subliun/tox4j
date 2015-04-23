package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupPrivateMessageCallback {

    void groupPrivateMessage(int groupNumber, int peerNumber, int timeDelta, @NotNull byte[] message);

}
