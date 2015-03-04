package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupPeerJoinCallback {

    void groupPeerJoin(int groupNumber, int peerNumber);

}
