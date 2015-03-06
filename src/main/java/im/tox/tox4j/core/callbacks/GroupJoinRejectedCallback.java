package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.enums.ToxGroupJoinRejected;

public interface GroupJoinRejectedCallback {

    void groupJoinRejected(int groupNumber, ToxGroupJoinRejected rejectedReason);

}
