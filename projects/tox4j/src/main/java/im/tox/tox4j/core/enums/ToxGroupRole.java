package im.tox.tox4j.core.enums;

public enum ToxGroupRole {
    /**
     * May kick and ban all other peers as well as set their role to anything (except founder).
     * Founders may also set the group password, toggle the privacy state, and set the peer limit.
     */
    FOUNDER,

    /**
     * May kick, ban and set the user and observer roles for peers below this role.
     */
    MODERATOR,

    /**
     * May communicate with other peers and change the group topic.
     */
    USER,

    /**
     * May observe the group and ignore peers; may not communicate with other peers or with the group.
     */
    OBSERVER,
}
