package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.enums.ToxGroupJoinRejected;

public interface GroupInviteRejectedCallback {

    void groupInviteRejected(int groupNumber, ToxGroupJoinRejected rejectedReason);

}
