package im.tox.tox4j.core.enums;

public enum ToxGroupJoinFail {
    /**
     * You are using the same nickname as someone who is already in the group.
     */
    NAME_TAKEN,

    /**
     * The group peer limit has been reached.
     */
    PEER_LIMIT,

    /**
     * You have supplied an invalid password.
     */
    INVALID_PASSWORD,

    /**
     * The join attempt failed due to an unspecified error. This often occurs when the group is
     * not found in the DHT.
     */
    UNKNOWN,
}
