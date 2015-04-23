package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.annotations.NotNull;

public interface GroupInviteCallback {

    void groupInvite(int friendNumber, @NotNull byte[] inviteData);

}
