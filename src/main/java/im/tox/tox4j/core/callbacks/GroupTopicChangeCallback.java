package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupTopicChangeCallback {

    void groupTopicChange(int groupNumber, int peerNumber, @NotNull byte[] topic);

}
