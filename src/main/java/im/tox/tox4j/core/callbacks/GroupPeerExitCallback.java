package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupPeerExitCallback {

    void groupPeerExit(int groupNumber, int peerNumber, @NotNull byte[] partMessage);

}
