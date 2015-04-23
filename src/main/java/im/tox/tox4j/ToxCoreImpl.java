package im.tox.tox4j;

import com.google.protobuf.InvalidProtocolBufferException;
import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.annotations.Nullable;
import im.tox.tox4j.core.AbstractToxCore;
import im.tox.tox4j.core.ToxConstants;
import im.tox.tox4j.core.ToxOptions;
import im.tox.tox4j.core.callbacks.*;
import im.tox.tox4j.core.enums.*;
import im.tox.tox4j.core.exceptions.*;
import im.tox.tox4j.core.proto.Core;

public final class ToxCoreImpl extends AbstractToxCore {

    static {
        System.loadLibrary("tox4j");
    }

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    @NotNull
    private static byte[] notNull(@Nullable byte[] bytes) {
        if (bytes == null) {
            bytes = EMPTY_BYTE_ARRAY;
        }
        return bytes;
    }

    @NotNull
    private static int[] notNull(@Nullable int[] ints) {
        if (ints == null) {
            ints = EMPTY_INT_ARRAY;
        }
        return ints;
    }

    private static void checkInfoNotNull(byte[] info) throws ToxSetInfoException {
        if (info == null) {
            throw new ToxSetInfoException(ToxSetInfoException.Code.NULL);
        }
    }

    /**
     * This field has package visibility for {@link ToxAvImpl}.
     */
    final int instanceNumber;
    /**
     * This field is set by {@link ToxAvImpl} on construction and reset back to null on close.
     */
    @Nullable ToxAvImpl av = null;

    private ConnectionStatusCallback connectionStatusCallback;
    private FriendNameCallback friendNameCallback;
    private FriendStatusMessageCallback friendStatusMessageCallback;
    private FriendStatusCallback friendStatusCallback;
    private FriendConnectionStatusCallback friendConnectionStatusCallback;
    private FriendTypingCallback friendTypingCallback;
    private ReadReceiptCallback readReceiptCallback;
    private FriendRequestCallback friendRequestCallback;
    private FriendMessageCallback friendMessageCallback;
    private FileControlCallback fileControlCallback;
    private FileRequestChunkCallback fileRequestChunkCallback;
    private FileReceiveCallback fileReceiveCallback;
    private FileReceiveChunkCallback fileReceiveChunkCallback;
    private GroupInviteCallback groupInviteCallback;
    private GroupMessageCallback groupMessageCallback;
    private GroupPrivateMessageCallback groupPrivateMessageCallback;
    private GroupActionCallback groupActionCallback;
    private GroupNickChangeCallback groupNickChangeCallback;
    private GroupTopicChangeCallback groupTopicChangeCallback;
    private GroupPeerJoinCallback groupPeerJoinCallback;
    private GroupPeerExitCallback groupPeerExitCallback;
    private GroupSelfJoinCallback groupSelfJoinCallback;
    private GroupPeerlistUpdateCallback groupPeerlistUpdateCallback;
    private GroupJoinRejectedCallback groupJoinRejectedCallback;
    private FriendLossyPacketCallback friendLossyPacketCallback;
    private FriendLosslessPacketCallback friendLosslessPacketCallback;

    private static native void playground(int instanceNumber);
    void playground() {
        playground(instanceNumber);
    }


    private static native int toxNew(
        @Nullable byte[] data,
        boolean ipv6Enabled,
        boolean udpEnabled,
        int proxyType,
        String proxyAddress,
        int proxyPort
    ) throws ToxNewException;

    public ToxCoreImpl(@NotNull ToxOptions options, @Nullable byte[] data) throws ToxNewException {
        instanceNumber = toxNew(
            data,
            options.isIpv6Enabled(),
            options.isUdpEnabled(),
            options.getProxyType().ordinal(),
            options.getProxyAddress(),
            options.getProxyPort()
        );
    }


    private static native void toxKill(int instanceNumber);

    @Override
    public void close() {
        if (av != null) {
            av.close();
        }
        toxKill(instanceNumber);
    }


    private static native void finalize(int instanceNumber);

    @Override
    public void finalize() throws Throwable {
        try {
            finalize(instanceNumber);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.finalize();
    }


    private static native byte[] toxSave(int instanceNumber);

    @Override
    public @NotNull byte[] save() {
        return toxSave(instanceNumber);
    }


    private static native void toxBootstrap(int instanceNumber, @NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException;
    private static native void toxAddTcpRelay(int instanceNumber, @NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException;

    private static void checkBootstrapArguments(int port, @Nullable byte[] public_key) {
        if (port < 0) {
            throw new IllegalArgumentException("Ports cannot be negative");
        }
        if (port > 65535) {
            throw new IllegalArgumentException("Ports cannot be larger than 65535");
        }
        if (public_key != null) {
            if (public_key.length < ToxConstants.PUBLIC_KEY_SIZE) {
                throw new IllegalArgumentException("Key too short, must be " + ToxConstants.PUBLIC_KEY_SIZE + " bytes");
            }
            if (public_key.length > ToxConstants.PUBLIC_KEY_SIZE) {
                throw new IllegalArgumentException("Key too long, must be " + ToxConstants.PUBLIC_KEY_SIZE + " bytes");
            }
        }
    }

    @Override
    public void bootstrap(@NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException {
        checkBootstrapArguments(port, public_key);
        toxBootstrap(instanceNumber, address, port, public_key);
    }

    @Override
    public void addTcpRelay(@NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException {
        checkBootstrapArguments(port, public_key);
        toxAddTcpRelay(instanceNumber, address, port, public_key);
    }


    @Override
    public void callbackConnectionStatus(ConnectionStatusCallback callback) {
        this.connectionStatusCallback = callback;
    }


    private static native int toxGetUdpPort(int instanceNumber) throws ToxGetPortException;

    @Override
    public int getUdpPort() throws ToxGetPortException {
        return toxGetUdpPort(instanceNumber);
    }


    private static native int toxGetTcpPort(int instanceNumber) throws ToxGetPortException;

    @Override
    public int getTcpPort() throws ToxGetPortException {
        return toxGetTcpPort(instanceNumber);
    }


    private static native @NotNull byte[] toxGetDhtId(int instanceNumber);

    @Override
    public @NotNull byte[] getDhtId() {
        return toxGetDhtId(instanceNumber);
    }


    private static native int toxIterationInterval(int instanceNumber);

    @Override
    public int iterationInterval() {
        return toxIterationInterval(instanceNumber);
    }


    private static @NotNull ToxConnection convert(@NotNull Core.Socket status) {
        switch (status) {
            case NONE: return ToxConnection.NONE;
            case TCP: return ToxConnection.TCP;
            case UDP: return ToxConnection.UDP;
        }
        throw new IllegalStateException("Bad enumerator: " + status);
    }

    private static @NotNull ToxStatus convert(@NotNull Core.FriendStatus.Kind status) {
        switch (status) {
            case NONE: return ToxStatus.NONE;
            case AWAY: return ToxStatus.AWAY;
            case BUSY: return ToxStatus.BUSY;
        }
        throw new IllegalStateException("Bad enumerator: " + status);
    }

    private static @NotNull ToxFileControl convert(@NotNull Core.FileControl.Kind control) {
        switch (control) {
            case RESUME: return ToxFileControl.RESUME;
            case PAUSE: return ToxFileControl.PAUSE;
            case CANCEL: return ToxFileControl.CANCEL;
        }
        throw new IllegalStateException("Bad enumerator: " + control);
    }

    private static @NotNull ToxMessageType convert(@NotNull Core.FriendMessage.Type type) {
        switch (type) {
            case NORMAL: return ToxMessageType.NORMAL;
            case ACTION: return ToxMessageType.ACTION;
        }
        throw new IllegalStateException("Bad enumerator: " + type);
    }

    private static @NotNull ToxGroupJoinRejected convert(@NotNull Core.GroupRejected.Kind kind) {
        switch (kind) {
            case NICK_TAKEN: return ToxGroupJoinRejected.NICK_TAKEN;
            case GROUP_FULL: return ToxGroupJoinRejected.GROUP_FULL;
            case INVITES_DISABLED: return ToxGroupJoinRejected.INVITES_DISABLED;
            case INVITE_FAILED: return ToxGroupJoinRejected.INVITE_FAILED;

        }
        throw new IllegalStateException("Bad enumerator: " + kind);
    }

    private static native @NotNull byte[] toxIteration(int instanceNumber);

    @Override
    public void iteration() {
        byte[] events = toxIteration(instanceNumber);
        Core.CoreEvents toxEvents;
        try {
            toxEvents = Core.CoreEvents.parseFrom(events);
        } catch (InvalidProtocolBufferException e) {
            // This would be very bad, meaning something went wrong in our own C++ code.
            throw new RuntimeException(e);
        }

        if (connectionStatusCallback != null) {
			for (Core.ConnectionStatus connectionStatus : toxEvents.getConnectionStatusList()) {
				connectionStatusCallback.connectionStatus(convert(connectionStatus.getConnectionStatus()));
			}
		}
        if (friendNameCallback != null) {
			for (Core.FriendName friendName : toxEvents.getFriendNameList()) {
				friendNameCallback.friendName(friendName.getFriendNumber(), friendName.getName().toByteArray());
			}
		}
        if (friendStatusMessageCallback != null) {
			for (Core.FriendStatusMessage friendStatusMessage : toxEvents.getFriendStatusMessageList()) {
				friendStatusMessageCallback.friendStatusMessage(friendStatusMessage.getFriendNumber(), friendStatusMessage.getMessage().toByteArray());
			}
		}
        if (friendStatusCallback != null) {
			for (Core.FriendStatus friendStatus : toxEvents.getFriendStatusList()) {
				friendStatusCallback.friendStatus(friendStatus.getFriendNumber(), convert(friendStatus.getStatus()));
			}
		}
        if (friendConnectionStatusCallback != null) {
			for (Core.FriendConnectionStatus friendConnectionStatus : toxEvents.getFriendConnectionStatusList()) {
				friendConnectionStatusCallback.friendConnectionStatus(friendConnectionStatus.getFriendNumber(), convert(friendConnectionStatus.getConnectionStatus()));
			}
		}
        if (friendTypingCallback != null) {
			for (Core.FriendTyping friendTyping : toxEvents.getFriendTypingList()) {
				friendTypingCallback.friendTyping(friendTyping.getFriendNumber(), friendTyping.getIsTyping());
			}
		}
        if (readReceiptCallback != null) {
			for (Core.ReadReceipt readReceipt : toxEvents.getReadReceiptList()) {
				readReceiptCallback.readReceipt(readReceipt.getFriendNumber(), readReceipt.getMessageId());
			}
		}
        if (friendRequestCallback != null) {
			for (Core.FriendRequest friendRequest : toxEvents.getFriendRequestList()) {
				friendRequestCallback.friendRequest(friendRequest.getPublicKey().toByteArray(), friendRequest.getTimeDelta(), friendRequest.getMessage().toByteArray());
			}
		}
        if (friendMessageCallback != null) {
			for (Core.FriendMessage friendMessage : toxEvents.getFriendMessageList()) {
                friendMessageCallback.friendMessage(friendMessage.getFriendNumber(), convert(friendMessage.getType()), friendMessage.getTimeDelta(), friendMessage.getMessage().toByteArray());
			}
		}
        if (fileControlCallback != null) {
			for (Core.FileControl fileControl : toxEvents.getFileControlList()) {
				fileControlCallback.fileControl(fileControl.getFriendNumber(), fileControl.getFileNumber(), convert(fileControl.getControl()));
			}
		}
        if (fileRequestChunkCallback != null) {
			for (Core.FileRequestChunk fileRequestChunk : toxEvents.getFileRequestChunkList()) {
				fileRequestChunkCallback.fileRequestChunk(fileRequestChunk.getFriendNumber(), fileRequestChunk.getFileNumber(), fileRequestChunk.getPosition(), fileRequestChunk.getLength());
			}
		}
        if (fileReceiveCallback != null) {
			for (Core.FileReceive fileReceive : toxEvents.getFileReceiveList()) {
				fileReceiveCallback.fileReceive(fileReceive.getFriendNumber(), fileReceive.getFileNumber(), fileReceive.getKind(), fileReceive.getFileSize(), fileReceive.getFilename().toByteArray());
			}
		}
        if (fileReceiveChunkCallback != null) {
			for (Core.FileReceiveChunk fileReceiveChunk : toxEvents.getFileReceiveChunkList()) {
				fileReceiveChunkCallback.fileReceiveChunk(fileReceiveChunk.getFriendNumber(), fileReceiveChunk.getFileNumber(), fileReceiveChunk.getPosition(), fileReceiveChunk.getData().toByteArray());
			}
		}
        if (groupInviteCallback != null) {
            for (Core.GroupInvite groupInvite : toxEvents.getGroupInviteList()) {
                groupInviteCallback.groupInvite(groupInvite.getFriendNumber(), groupInvite.getInviteData().toByteArray());
            }
        }
        if (groupMessageCallback != null) {
            for (Core.GroupMessage groupMessage : toxEvents.getGroupMessageList()) {
                groupMessageCallback.groupMessage(groupMessage.getGroupNumber(), groupMessage.getPeerNumber(), groupMessage.getTimeDelta(), groupMessage.getMessage().toByteArray());
            }
        }
        if (groupPrivateMessageCallback != null) {
            for (Core.GroupPrivateMessage groupPrivateMessage : toxEvents.getGroupPrivateMessageList()) {
                groupPrivateMessageCallback.groupPrivateMessage(groupPrivateMessage.getGroupNumber(), groupPrivateMessage.getPeerNumber(), groupPrivateMessage.getTimeDelta(), groupPrivateMessage.getMessage().toByteArray());
            }
        }
        if (groupActionCallback != null) {
            for (Core.GroupAction groupAction : toxEvents.getGroupActionList()) {
                groupActionCallback.groupAction(groupAction.getGroupNumber(), groupAction.getPeerNumber(), groupAction.getTimeDelta(), groupAction.getMessage().toByteArray());
            }
        }
        if (groupNickChangeCallback != null) {
            for (Core.GroupNickChange groupNickChange : toxEvents.getGroupNickChangeList()) {
                groupNickChangeCallback.groupNickChange(groupNickChange.getGroupNumber(), groupNickChange.getPeerNumber(), groupNickChange.getNewNick().toByteArray());
            }
        }
        if (groupTopicChangeCallback != null) {
            for (Core.GroupTopicChange groupTopicChange : toxEvents.getGroupTopicChangeList()) {
                groupTopicChangeCallback.groupTopicChange(groupTopicChange.getGroupNumber(), groupTopicChange.getPeerNumber(), groupTopicChange.getTopic().toByteArray());
            }
        }
        if (groupPeerJoinCallback != null) {
            for (Core.GroupPeerJoin groupPeerJoin : toxEvents.getGroupPeerJoinList()) {
                groupPeerJoinCallback.groupPeerJoin(groupPeerJoin.getGroupNumber(), groupPeerJoin.getPeerNumber());
            }
        }
        if (groupPeerExitCallback != null) {
            for (Core.GroupPeerExit groupPeerExit : toxEvents.getGroupPeerExitList()) {
                groupPeerExitCallback.groupPeerExit(groupPeerExit.getGroupNumber(), groupPeerExit.getPeerNumber(), groupPeerExit.getPartMessage().toByteArray());
            }
        }
        if (groupSelfJoinCallback != null) {
            for (Core.GroupSelfJoin groupSelfJoin : toxEvents.getGroupSelfJoinList()) {
                groupSelfJoinCallback.groupSelfJoin(groupSelfJoin.getGroupNumber());
            }
        }
        if (groupPeerlistUpdateCallback != null) {
            for (Core.GroupPeerlistUpdate groupPeerlistUpdate : toxEvents.getGroupPeerlistUpdateList()) {
                groupPeerlistUpdateCallback.groupPeerlistUpdate(groupPeerlistUpdate.getGroupNumber());
            }
        }
        if (groupJoinRejectedCallback != null) {
            for (Core.GroupRejected groupRejected : toxEvents.getGroupRejectedList()) {
                groupJoinRejectedCallback.groupJoinRejected(groupRejected.getGroupNumber(), convert(groupRejected.getType()));
            }
        }
        if (friendLossyPacketCallback != null) {
			for (Core.FriendLossyPacket friendLossyPacket : toxEvents.getFriendLossyPacketList()) {
				friendLossyPacketCallback.friendLossyPacket(friendLossyPacket.getFriendNumber(), friendLossyPacket.getData().toByteArray());
			}
		}
        if (friendLosslessPacketCallback != null) {
			for (Core.FriendLosslessPacket friendLosslessPacket : toxEvents.getFriendLosslessPacketList()) {
				friendLosslessPacketCallback.friendLosslessPacket(friendLosslessPacket.getFriendNumber(), friendLosslessPacket.getData().toByteArray());
			}
		}
    }


    private static native @NotNull byte[] toxSelfGetPublicKey(int instanceNumber);

    @Override
    public @NotNull byte[] getPublicKey() {
        return toxSelfGetPublicKey(instanceNumber);
    }


    private static native @NotNull byte[] toxSelfGetSecretKey(int instanceNumber);

    @Override
    public @NotNull byte[] getSecretKey() {
        return toxSelfGetSecretKey(instanceNumber);
    }


    private static native void toxSelfSetNospam(int instanceNumber, int nospam);

    @Override
    public void setNospam(int nospam) {
        toxSelfSetNospam(instanceNumber, nospam);
    }


    private static native int toxSelfGetNospam(int instanceNumber);

    @Override
    public int getNospam() {
        return toxSelfGetNospam(instanceNumber);
    }


    private static native @NotNull byte[] toxSelfGetAddress(int instanceNumber);

    @Override
    public @NotNull byte[] getAddress() {
        return toxSelfGetAddress(instanceNumber);
    }


    private static native void toxSelfSetName(int instanceNumber, @NotNull byte[] name) throws ToxSetInfoException;

    @Override
    public void setName(@NotNull byte[] name) throws ToxSetInfoException {
        checkInfoNotNull(name);
        toxSelfSetName(instanceNumber, name);
    }


    private static native @Nullable byte[] toxSelfGetName(int instanceNumber);

    @Override
    public @NotNull byte[] getName() {
        return notNull(toxSelfGetName(instanceNumber));
    }


    private static native void toxSelfSetStatusMessage(int instanceNumber, byte[] message) throws ToxSetInfoException;

    @Override
    public void setStatusMessage(byte[] message) throws ToxSetInfoException {
        checkInfoNotNull(message);
        toxSelfSetStatusMessage(instanceNumber, message);
    }


    private static native @Nullable byte[] toxSelfGetStatusMessage(int instanceNumber);

    @Override
    public @NotNull byte[] getStatusMessage() {
        return notNull(toxSelfGetStatusMessage(instanceNumber));
    }


    private static native void toxSelfSetStatus(int instanceNumber, int status);

    @Override
    public void setStatus(@NotNull ToxStatus status) {
        toxSelfSetStatus(instanceNumber, status.ordinal());
    }


    private static native int toxSelfGetStatus(int instanceNumber);

    @Override
    public @NotNull ToxStatus getStatus() {
        return ToxStatus.values()[toxSelfGetStatus(instanceNumber)];
    }


    private static void checkLength(@NotNull String name, @NotNull byte[] bytes, int expectedSize) {
        //noinspection ConstantConditions
        if (bytes != null) {
            if (bytes.length < expectedSize) {
                throw new IllegalArgumentException(name + " too short, must be " + expectedSize + " bytes");
            }
            if (bytes.length > expectedSize) {
                throw new IllegalArgumentException(name + " too long, must be " + expectedSize + " bytes");
            }
        }
    }

    private static native int toxFriendAdd(int instanceNumber, @NotNull byte[] address, @NotNull byte[] message) throws ToxFriendAddException;

    @Override
    public int addFriend(@NotNull byte[] address, @NotNull byte[] message) throws ToxFriendAddException {
        checkLength("Friend Address", address, ToxConstants.ADDRESS_SIZE);
        return toxFriendAdd(instanceNumber, address, message);
    }


    private static native int toxFriendAddNorequest(int instanceNumber, @NotNull byte[] publicKey) throws ToxFriendAddException;

    @Override
    public int addFriendNoRequest(@NotNull byte[] publicKey) throws ToxFriendAddException {
        checkLength("Public Key", publicKey, ToxConstants.PUBLIC_KEY_SIZE);
        return toxFriendAddNorequest(instanceNumber, publicKey);
    }


    private static native void toxFriendDelete(int instanceNumber, int friendNumber) throws ToxFriendDeleteException;

    @Override
    public void deleteFriend(int friendNumber) throws ToxFriendDeleteException {
        toxFriendDelete(instanceNumber, friendNumber);
    }


    private static native int toxFriendByPublicKey(int instanceNumber, @NotNull byte[] publicKey) throws ToxFriendByPublicKeyException;

    @Override
    public int getFriendByPublicKey(@NotNull byte[] publicKey) throws ToxFriendByPublicKeyException {
        return toxFriendByPublicKey(instanceNumber, publicKey);
    }


    private static native @NotNull byte[] toxFriendGetPublicKey(int instanceNumber, int friendNumber) throws ToxFriendGetPublicKeyException;

    @Override
    public @NotNull byte[] getPublicKey(int friendNumber) throws ToxFriendGetPublicKeyException {
        return toxFriendGetPublicKey(instanceNumber, friendNumber);
    }


    private static native boolean toxFriendExists(int instanceNumber, int friendNumber);

    @Override
    public boolean friendExists(int friendNumber) {
        return toxFriendExists(instanceNumber, friendNumber);
    }


    private static native @NotNull int[] toxFriendList(int instanceNumber);

    @Override
    public @NotNull int[] getFriendList() {
        return notNull(toxFriendList(instanceNumber));
    }


    @Override
    public void callbackFriendName(FriendNameCallback callback) {
        this.friendNameCallback = callback;
    }

    @Override
    public void callbackFriendStatusMessage(FriendStatusMessageCallback callback) {
        this.friendStatusMessageCallback = callback;
    }

    @Override
    public void callbackFriendStatus(FriendStatusCallback callback) {
        this.friendStatusCallback = callback;
    }

    @Override
    public void callbackFriendConnected(FriendConnectionStatusCallback callback) {
        this.friendConnectionStatusCallback = callback;
    }

    @Override
    public void callbackFriendTyping(FriendTypingCallback callback) {
        this.friendTypingCallback = callback;
    }


    private static native void toxSelfSetTyping(int instanceNumber, int friendNumber, boolean typing) throws ToxSetTypingException;

    @Override
    public void setTyping(int friendNumber, boolean typing) throws ToxSetTypingException {
        toxSelfSetTyping(instanceNumber, friendNumber, typing);
    }


    private static native int toxSendMessage(int instanceNumber, int friendNumber, int type, int timeDelta, @NotNull byte[] message) throws ToxSendMessageException;

    @Override
    public int sendMessage(int friendNumber, @NotNull ToxMessageType type, int timeDelta, @NotNull byte[] message) throws ToxSendMessageException {
        return toxSendMessage(instanceNumber, friendNumber, type.ordinal(), timeDelta, message);
    }


    @Override
    public void callbackReadReceipt(ReadReceiptCallback callback) {
        this.readReceiptCallback = callback;
    }

    @Override
    public void callbackFriendRequest(FriendRequestCallback callback) {
        this.friendRequestCallback = callback;
    }

    @Override
    public void callbackFriendMessage(FriendMessageCallback callback) {
        this.friendMessageCallback = callback;
    }


    private static native void toxFileControl(int instanceNumber, int friendNumber, int fileNumber, int control) throws ToxFileControlException;

    @Override
    public void fileControl(int friendNumber, int fileNumber, @NotNull ToxFileControl control) throws ToxFileControlException {
        toxFileControl(instanceNumber, friendNumber, fileNumber, control.ordinal());
    }

    @Override
    public void callbackFileControl(FileControlCallback callback) {
        this.fileControlCallback = callback;
    }


    private static native void toxFileSendSeek(int instanceNumber, int friendNumber, int fileNumber, long position) throws ToxFileSendSeekException;

    @Override
    public void fileSendSeek(int friendNumber, int fileNumber, long position) throws ToxFileSendSeekException {
        toxFileSendSeek(instanceNumber, friendNumber, fileNumber, position);
    }


    private static native int toxFileSend(int instanceNumber, int friendNumber, int kind, long fileSize, @Nullable byte[] fileId, @NotNull byte[] filename) throws ToxFileSendException;

    @Override
    public int fileSend(int friendNumber, int kind, long fileSize, @Nullable byte[] fileId, @NotNull byte[] filename) throws ToxFileSendException {
        return toxFileSend(instanceNumber, friendNumber, kind, fileSize, fileId, filename);
    }


    private static native void toxFileSendChunk(int instanceNumber, int friendNumber, int fileNumber, long position, @NotNull byte[] data) throws ToxFileSendChunkException;

    @Override
    public void fileSendChunk(int friendNumber, int fileNumber, long position, @NotNull byte[] data) throws ToxFileSendChunkException {
        toxFileSendChunk(instanceNumber, friendNumber, fileNumber, position, data);
    }


    private static native byte[] toxFileGetFileId(int friendNumber, int fileNumber) throws ToxFileGetInfoException;

    public byte[] fileGetFileId(int friendNumber, int fileNumber) throws ToxFileGetInfoException {
      	return toxFileGetFileId(friendNumber, fileNumber);
    }


    @Override
    public void callbackFileRequestChunk(FileRequestChunkCallback callback) {
        this.fileRequestChunkCallback = callback;
    }

    @Override
    public void callbackFileReceive(FileReceiveCallback callback) {
        this.fileReceiveCallback = callback;
    }

    @Override
    public void callbackFileReceiveChunk(FileReceiveChunkCallback callback) {
        this.fileReceiveChunkCallback = callback;
    }


    private static native int toxGroupAcceptInvite(int instanceNumber, @NotNull byte[] inviteData) throws ToxGroupAcceptInviteException;

    @Override
    public int acceptGroupInvite(@NotNull byte[] inviteData) throws ToxGroupAcceptInviteException { return toxGroupAcceptInvite(instanceNumber, inviteData); }

    private static native int toxGroupNew(int instanceNumber, @NotNull byte[] name);

    @Override
    public int newGroup(byte[] name) { return toxGroupNew(instanceNumber, name); }

    private static native int toxGroupNewJoin(int instanceNumber, @NotNull byte[] inviteKey);

    @Override
    public int joinGroup(byte[] inviteKey) {
        return toxGroupNewJoin(instanceNumber, inviteKey);
    }

    private static native int toxGroupReconnect(int instanceNumber, int groupNumber);

    @Override
    public int reconnectGroup(int groupNumber) { return toxGroupReconnect(instanceNumber, groupNumber); }

    private static native void toxGroupDelete(int instanceNumber, int groupNumber, @NotNull byte[] partMessage);

    @Override
    public void deleteGroup(int groupNumber, @NotNull byte[] partMessage) { toxGroupDelete(instanceNumber, groupNumber, partMessage); }

    private static native void toxGroupMessageSend(int instanceNumber, int groupNumber, @NotNull byte[] message);

    @Override
    public void sendGroupMessage(int groupNumber, @NotNull byte[] message) { toxGroupMessageSend(instanceNumber, groupNumber, message); }

    private static native void toxGroupPrivateMessageSend(int instanceNumber, int groupNumber, int peerNumber, @NotNull byte[] message);

    @Override
    public void sendGroupPrivateMessage(int groupNumber, int peerNumber, @NotNull byte[] message) { toxGroupPrivateMessageSend(instanceNumber, groupNumber, peerNumber, message); }

    private static native void toxGroupActionSend(int instanceNumber, int groupNumber, @NotNull byte[] message);

    @Override
    public void sendGroupAction(int groupNumber, @NotNull byte[] message) { toxGroupActionSend(instanceNumber, groupNumber, message); }

    private static native void toxGroupSetSelfName(int instanceNumber, int groupNumber, @NotNull byte[] name);

    @Override
    public void setGroupSelfName(int groupNumber, @NotNull byte[] name) { toxGroupSetSelfName(instanceNumber, groupNumber, name); }

    private static native byte[] toxGroupGetPeerName(int instanceNumber, int groupNumber, int peerNumber);

    @NotNull
    @Override
    public byte[] getGroupPeerName(int groupNumber, int peerNumber) { return toxGroupGetPeerName(instanceNumber, groupNumber, peerNumber); }

    private static native byte[] toxGroupGetSelfName(int instanceNumber, int groupNumber);

    @NotNull
    @Override
    public byte[] getGroupSelfName(int groupNumber) { return toxGroupGetSelfName(instanceNumber, groupNumber); }

    private static native void toxGroupSetTopic(int instanceNumber, int groupNumber, byte[] topic);

    @Override
    public void setGroupTopic(int groupNumber, byte[] topic) { toxGroupSetTopic(instanceNumber, groupNumber, topic); }

    private static native byte[] toxGroupGetTopic(int instanceNumber, int groupNumber);

    @NotNull
    @Override
    public byte[] getGroupTopic(int groupNumber) { return toxGroupGetTopic(instanceNumber, groupNumber); }

    private static native byte[] toxGroupGetGroupName(int instanceNumber, int groupNumber);

    @NotNull
    @Override
    public byte[] getGroupName(int groupNumber) { return toxGroupGetGroupName(instanceNumber, groupNumber); }

    private static native void toxGroupSetStatus(int instanceNumber, int groupNumber, int status);

    @Override
    public void setGroupSelfStatus(int groupNumber, ToxGroupStatus status) {
        toxGroupSetStatus(instanceNumber, groupNumber, status.ordinal());
    }

    private static native int toxGroupGetStatus(int instanceNumber, int groupNumber, int peerNumber);

    @Override
    public ToxGroupStatus getGroupPeerStatus(int groupNumber, int peerNumber) {
        return ToxGroupStatus.values()[toxGroupGetStatus(instanceNumber, groupNumber, peerNumber)];
    }

    private static native int toxGroupGetRole(int instanceNumber,int groupNumber, int peerNumber);

    @Override
    public ToxGroupRole getGroupPeerRole(int groupNumber, int peerNumber) {
        return ToxGroupRole.values()[toxGroupGetRole(instanceNumber, groupNumber, peerNumber)];
    }

    private static native byte[] toxGroupGetChatId(int instanceNumber, int groupNumber);

    @Override
    public byte[] getGroupChatId(int groupNumber) { return toxGroupGetChatId(instanceNumber, groupNumber); }

    private static native int toxGroupGetNumberPeers(int instanceNumber, int groupNumber);

    @Override
    public int getGroupNumberPeers(int groupNumber) { return toxGroupGetNumberPeers(instanceNumber, groupNumber); }

    private static native int toxGroupCountGroups(int instanceNumber);

    @Override
    public int getActiveGroupsCount() { return toxGroupCountGroups(instanceNumber); }

    @Override
    public void callbackGroupInvite(@Nullable GroupInviteCallback callback) { this.groupInviteCallback = callback; }

    @Override
    public void callbackGroupMessage(GroupMessageCallback callback) {
        this.groupMessageCallback = callback;
    }

    @Override
    public void callbackGroupPrivateMessage(@Nullable GroupPrivateMessageCallback callback) { this.groupPrivateMessageCallback = callback; }

    @Override
    public void callbackGroupAction(@Nullable GroupActionCallback callback) { this.groupActionCallback = callback; }

    @Override
    public void callbackGroupNickChange(@Nullable GroupNickChangeCallback callback) { this.groupNickChangeCallback = callback; }

    @Override
    public void callbackGroupTopicChange(@Nullable GroupTopicChangeCallback callback) { this.groupTopicChangeCallback = callback; }

    @Override
    public void callbackPeerJoin(@Nullable GroupPeerJoinCallback callback) { this.groupPeerJoinCallback = callback; }

    @Override
    public void callbackPeerExit(@Nullable GroupPeerExitCallback callback) { this.groupPeerExitCallback = callback; }

    @Override
    public void callbackGroupSelfJoin(@Nullable GroupSelfJoinCallback callback) { this.groupSelfJoinCallback = callback; }

    @Override
    public void callbackGroupPeerlistUpdate(@Nullable GroupPeerlistUpdateCallback callback) { this.groupPeerlistUpdateCallback = callback; }

    @Override
    public void callbackGroupJoinRejected(@Nullable GroupJoinRejectedCallback callback) { this.groupJoinRejectedCallback = callback; }


    private static native void toxSendLossyPacket(int instanceNumber, int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException;

    @Override
    public void sendLossyPacket(int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException {
        toxSendLossyPacket(instanceNumber, friendNumber, data);
    }

    @Override
    public void callbackFriendLossyPacket(FriendLossyPacketCallback callback) {
        this.friendLossyPacketCallback = callback;
    }


    private static native void toxSendLosslessPacket(int instanceNumber, int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException;

    @Override
    public void sendLosslessPacket(int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException {
        toxSendLosslessPacket(instanceNumber, friendNumber, data);
    }

    @Override
    public void callbackFriendLosslessPacket(FriendLosslessPacketCallback callback) {
        this.friendLosslessPacketCallback = callback;
    }

}
