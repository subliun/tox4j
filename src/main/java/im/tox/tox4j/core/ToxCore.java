package im.tox.tox4j.core;

import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.annotations.Nullable;
import im.tox.tox4j.core.callbacks.*;
import im.tox.tox4j.core.enums.*;
import im.tox.tox4j.core.exceptions.*;
import im.tox.tox4j.core.proto.Core;

import java.io.Closeable;

/**
 * Interface for a basic wrapper of tox chat functionality.
 * <p>
 * This interface is designed to be thread-safe. However, once {@link #close()} has been called, all subsequent calls
 * will result in {@link im.tox.tox4j.exceptions.ToxKilledException} being thrown. When one thread invokes {@link #close()},
 * all other threads with pending calls will throw. The exception is unchecked, as it should not occur in a normal
 * execution flow. To prevent it from occurring in a multi-threaded environment, all additional threads should be stopped
 * before one thread invokes {@link #close()}, or appropriate exception handlers should be installed in all threads.
 */
public interface ToxCore extends Closeable {

    /**
     * Shut down the tox instance.
     * <p>
     * Once this method has been called, all other calls on this instance will throw
     * {@link im.tox.tox4j.exceptions.ToxKilledException}. A closed instance cannot be reused, a new instance must be created.
     */
    @Override
    void close();

    /**
     * Save the current tox instance (friend list etc).
     *
     * @return a byte array containing the tox instance
     */
    @NotNull
    byte[] save();

    /**
     * Bootstrap into the tox network.
     * <p>
     * May connect via UDP and/or TCP, depending of the settings of the Tox instance.
     *
     * @param address    the hostname, or an IPv4/IPv6 address of the node.
     * @param port       the port of the node.
     * @param public_key the public key of the node.
     * @throws ToxBootstrapException if an error occurred.
     */
    void bootstrap(@NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException;

    /**
     * Add another TCP relay in addition to the one passed to bootstrap.
     * <p>
     * Can also be used to add the same node the instance was bootstrapped with, but with a different port.
     *
     * @param address    the hostname, or an IPv4/IPv6 address of the node.
     * @param port       the TCP port the node is running a relay on.
     * @param public_key the public key of the node.
     * @throws ToxBootstrapException if an error occurred.
     */
    void addTcpRelay(@NotNull String address, int port, @NotNull byte[] public_key) throws ToxBootstrapException;

    /**
     * Sets the callback for connection status changes.
     *
     * @param callback the callback.
     */
    void callbackConnectionStatus(@Nullable ConnectionStatusCallback callback);

    /**
     * Get the UDP port this instance is bound to.
     *
     * @return the UDP port this instance is bound to.
     * @throws ToxGetPortException if an error occurred
     */
    int getUdpPort() throws ToxGetPortException;

    /**
     * Get the port this instance is serving as a TCP relay on.
     *
     * @return the TCP port this instance is bound to.
     * @throws ToxGetPortException if an error occurred
     */
    int getTcpPort() throws ToxGetPortException;

    /**
     * Get the temporary DHT public key for this instance.
     *
     * @return the temporary DHT public key.
     */
    @NotNull
    byte[] getDhtId();

    /**
     * Get the time in milliseconds until {@link #iteration()} should be called again.
     *
     * @return the time in milliseconds until {@link #iteration()} should be called again.
     */
    int iterationInterval();

    /**
     * The main tox loop.
     * <p>
     * This should be invoked every {@link #iterationInterval()} milliseconds.
     */
    void iteration();

    /**
     * Gets our own Client ID (public key).
     *
     * @return our own Client ID.
     */
    @NotNull
    byte[] getClientId();

    /**
     * Gets our own secret key.
     *
     * @return our own secret key.
     */
    @NotNull
    byte[] getPrivateKey();

    /**
     * Set the nospam number for our address.
     * <p>
     * Setting the nospam makes it impossible for others to send us friend requests that contained the old nospam number.
     *
     * @param noSpam the new nospam number.
     */
    void setNospam(int noSpam);

    /**
     * Get our current nospam number.
     *
     * @return the current nospam number.
     */
    int getNospam();

    /**
     * Get our current tox address to give to friends.
     * <p>
     * The format is the following: [Client ID (32 bytes)][nospam number (4 bytes)][checksum (2 bytes)]. After a call to
     * {@link #setNospam(int)}, the old address can no longer be used to send friend requests to this instance.
     *
     * @return our current tox address.
     */
    @NotNull
    byte[] getAddress();

    /**
     * Set our nickname.
     * <p>
     * Cannot be longer than {@link ToxConstants#MAX_NAME_LENGTH} bytes.
     *
     * @param name our name.
     * @throws ToxSetInfoException if an error occurs.
     */
    void setName(@Nullable byte[] name) throws ToxSetInfoException;

    /**
     * Get our own nickname. May be null if the nickname was empty.
     *
     * @return our nickname.
     */
    @NotNull
    byte[] getName();

    /**
     * Set our status message.
     * <p>
     * Cannot be longer than {@link ToxConstants#MAX_STATUS_MESSAGE_LENGTH} bytes.
     *
     * @param message the status message to set.
     * @throws ToxSetInfoException if an error occurs.
     */
    void setStatusMessage(@Nullable byte[] message) throws ToxSetInfoException;

    /**
     * Gets our own status message. May be null if the status message was empty.
     *
     * @return our status message.
     */
    @NotNull
    byte[] getStatusMessage();

    /**
     * Set our status.
     *
     * @param status status to set.
     */
    void setStatus(@NotNull ToxStatus status);

    /**
     * Get our status.
     *
     * @return our status.
     */
    @NotNull
    ToxStatus getStatus();

    /**
     * Adds a new friend by Friend Address.
     *
     * @param address the address to add as a friend ({@link ToxConstants#ADDRESS_SIZE} bytes).
     * @param message the message to send with the friend request (must not be empty).
     * @return the new friend's friend number.
     * @throws im.tox.tox4j.core.exceptions.ToxFriendAddException if an error occurred.
     * @throws java.lang.IllegalArgumentException if the Friend Address was not the right length.
     */
    int addFriend(@NotNull byte[] address, @NotNull byte[] message) throws ToxFriendAddException;

    /**
     * Add the specified Client ID as friend without sending a friend request.
     * <p>
     * This is mostly used for confirming incoming friend requests.
     *
     * @param clientId the Client ID to add as a friend ({@link ToxConstants#CLIENT_ID_SIZE} bytes).
     * @return the new friend's friend number.
     * @throws im.tox.tox4j.core.exceptions.ToxFriendAddException if an error occurred.
     * @throws java.lang.IllegalArgumentException if the Client ID was not the right length.
     */
    int addFriendNoRequest(@NotNull byte[] clientId) throws ToxFriendAddException;

    /**
     * Deletes the specified friend.
     *
     * @param friendNumber the friend number to delete.
     * @throws im.tox.tox4j.core.exceptions.ToxFriendDeleteException if an error occurrs.
     */
    void deleteFriend(int friendNumber) throws ToxFriendDeleteException;

    /**
     * Gets the friend number for the specified Client ID.
     *
     * @param clientId the Client ID.
     * @return the friend number that is associated with the Client ID.
     * @throws im.tox.tox4j.core.exceptions.ToxFriendByClientIdException if an error occurs.
     */
    int getFriendByClientId(@NotNull byte[] clientId) throws ToxFriendByClientIdException;

    /**
     * Gets the Client ID for the specified friend number.
     *
     * @param friendNumber the friend number.
     * @return the Client ID associated with the friend number.
     * @throws im.tox.tox4j.core.exceptions.ToxFriendGetClientIdException if an error occurs.
     */
    @NotNull
    byte[] getClientId(int friendNumber) throws ToxFriendGetClientIdException;

    /**
     * Checks whether a friend with the specified friend number exists.
     * <p>
     * If this function returns <code>true</code>, the return value is valid until the friend is deleted. If
     * <code>false</code> is returned, the return value is valid until either of {@link #addFriend(byte[], byte[])}
     * {@link #addFriendNoRequest(byte[])} is invoked.
     *
     * @param friendNumber the friend number to check.
     * @return true if such a friend exists.
     */
    boolean friendExists(int friendNumber);

    /**
     * Get an array of currently valid friend numbers.
     * <p>
     * This list is valid until either of the following is invoked: {@link #deleteFriend(int)}, {@link #addFriend(byte[], byte[])},
     * {@link #addFriendNoRequest(byte[])}.
     *
     * @return an array containing the currently valid friend numbers. Returns the empty int array if there are no friends.
     */
    @NotNull
    int[] getFriendList();

    /**
     * Set the callback for friend name changes.
     *
     * @param callback the callback.
     */
    void callbackFriendName(@Nullable FriendNameCallback callback);

    /**
     * Set the callback for friend status message changes.
     *
     * @param callback the callback.
     */
    void callbackFriendStatusMessage(@Nullable FriendStatusMessageCallback callback);

    /**
     * Set the callback for friend message changes.
     *
     * @param callback the callback.
     */
    void callbackFriendStatus(@Nullable FriendStatusCallback callback);

    /**
     * Set the callback for friend connection changes.
     *
     * @param callback the callback.
     */
    void callbackFriendConnected(@Nullable FriendConnectionStatusCallback callback);

    /**
     * Set the callback for friend typing changes.
     *
     * @param callback the callback.
     */
    void callbackFriendTyping(@Nullable FriendTypingCallback callback);

    /**
     * Tell friend number whether or not we are currently typing.
     *
     * @param friendNumber the friend number to set typing status for.
     * @param typing       <code>true</code> if we are currently typing.
     * @throws im.tox.tox4j.core.exceptions.ToxSetTypingException if an error occurred.
     */
    void setTyping(int friendNumber, boolean typing) throws ToxSetTypingException;

    int sendMessage(int friendNumber, @NotNull byte[] message) throws ToxSendMessageException;

    int sendAction(int friendNumber, @NotNull byte[] action) throws ToxSendMessageException;

    void callbackReadReceipt(@Nullable ReadReceiptCallback callback);

    void callbackFriendRequest(@Nullable FriendRequestCallback callback);

    void callbackFriendMessage(@Nullable FriendMessageCallback callback);

    void callbackFriendAction(@Nullable FriendActionCallback callback);

    void fileControl(int friendNumber, int fileNumber, @NotNull ToxFileControl control) throws ToxFileControlException;

    void callbackFileControl(@Nullable FileControlCallback callback);

    int fileSend(int friendNumber, @NotNull ToxFileKind kind, long fileSize, @NotNull byte[] filename) throws ToxFileSendException;

    void fileSendChunk(int friendNumber, int fileNumber, @NotNull byte[] data) throws ToxFileSendChunkException;

    void callbackFileRequestChunk(@Nullable FileRequestChunkCallback callback);

    void callbackFileReceive(@Nullable FileReceiveCallback callback);

    void callbackFileReceiveChunk(@Nullable FileReceiveChunkCallback callback);

    /** Joins a groupchat using the supplied group key.
     *
     * @return groupNumber
     */
    int joinGroup(byte[] inviteKey);

    /**
     * Deletes groupNumber's group chat and sends an optional parting message to group peers
     * The maximum parting message length is TOX_MAX_GROUP_PART_LENGTH.
     */
    void deleteGroup(int groupNumber, @NotNull byte[] partMessage);

    /** Sends a groupchat message to group groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
     */
    void sendGroupMessage(int groupNumber, @NotNull byte[] message);

    /** Sends a private message to peernumber in group groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
     */
    void sendGroupPrivateMessage(int groupNumber, int peerNumber, @NotNull byte[] message);

    /** Sends a groupchat action message to groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
     */
    void sendGroupAction(int groupNumber, @NotNull byte[] message);

    /** Sets your name for groupnumber. length should be no larger than TOX_MAX_NAME_LENGTH bytes.
     */
    void setGroupSelfName(int groupNumber, byte[] name);

    /**
     * Get peernumber's name in groupnumber's group chat.
     */
    byte[] getGroupPeerName(int groupNumber, int peerNumber);

    /**
     * Get your own name for groupnumber's group.
     */
    byte[] getGroupSelfName(int groupNumber);

    /**
     * Sets groupnumber's topic.
     */
    void setGroupTopic(int groupNumber, byte[] topic);

    /** Gets groupnumber's topic.
     */
    byte[] getGroupTopic(int groupNumber);

    /** Gets groupnumber's group name.
     */
    byte[] getGroupName(int groupNumber);

    /** Sets your status for groupnumber.
     */
    void setGroupSelfStatus(int groupNumber, ToxGroupStatus status);

    /** Get peernumber's status in groupnumber's group chat.
     *
     * @return a TOX_GROUP_STATUS on success.
     * @return TOX_GS_INVALID on failure.
     */
    ToxGroupStatus getGroupPeerStatus(int groupNumber, int peernumber);

    /* Get peernumber's group role in groupnumber's group chat.
      *
      * @return a TOX_GROUP_ROLE on success.
      * @return TOX_GR_INVALID on failure.
      */
    ToxGroupRole getGroupPeerRole(int groupNumber, int peernumber);

    /**
     * Get the chat id of the groupchat from the groupnumber.
     */
    byte[] getGroupChatId(int groupNumber);

    /**
     * @return the number of peers in groupnumber.
     */
    int getGroupNumberPeers(int groupNumber);

    void callbackGroupInvite(@Nullable GroupInviteCallback callback);

    void callbackGroupMessage(@Nullable GroupMessageCallback callback);

    void callbackGroupPrivateMessage(@Nullable GroupPrivateMessageCallback callback);

    void callbackGroupAction(@Nullable GroupActionCallback callback);

    void callbackGroupNickChange(@Nullable GroupNickChangeCallback callback);

    void callbackGroupTopicChange(@Nullable GroupTopicChangeCallback callback);

    void callbackPeerJoin(@Nullable GroupPeerJoinCallback callback);

    void callbackPeerExit(@Nullable GroupPeerExitCallback callback);

    void callbackGroupSelfJoin(@Nullable GroupSelfJoinCallback callback);

    void callbackGroupPeerlistUpdate(@Nullable GroupPeerlistUpdateCallback callback);

    void callbackGroupSelfTimeout(@Nullable GroupSelfTimeoutCallback callback);

    void callbackGroupJoinRejected(@Nullable GroupJoinRejectedCallback callback);

    void sendLossyPacket(int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException;

    void callbackFriendLossyPacket(@Nullable FriendLossyPacketCallback callback);

    void sendLosslessPacket(int friendNumber, @NotNull byte[] data) throws ToxSendCustomPacketException;

    void callbackFriendLosslessPacket(@Nullable FriendLosslessPacketCallback callback);


    /**
     * Convenience method to set all event handlers at once.
     *
     * @param handler An event handler capable of handling all Tox events.
     */
    void callback(@Nullable ToxEventListener handler);

}
