package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.proto.Core;

public interface ToxEventListener extends
        ConnectionStatusCallback,
        FileControlCallback,
        FileReceiveCallback,
        FileReceiveChunkCallback,
        FileRequestChunkCallback,
        FriendActionCallback,
        FriendConnectionStatusCallback,
        FriendMessageCallback,
        FriendNameCallback,
        FriendRequestCallback,
        FriendStatusCallback,
        FriendStatusMessageCallback,
        FriendTypingCallback,
        FriendLosslessPacketCallback,
        FriendLossyPacketCallback,
        ReadReceiptCallback,
        GroupInviteCallback
{
}
