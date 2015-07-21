package im.tox.tox4j.core

import java.io.Closeable

import im.tox.tox4j.core.callbacks._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Interface for a basic wrapper of tox chat functionality.
 *
 * This interface is designed to be thread-safe. However, once [[ToxCore.close]] has been called, all subsequent calls
 * will result in [[im.tox.tox4j.exceptions.ToxKilledException]] being thrown. When one thread invokes
 * [[ToxCore.close]], all other threads with pending calls will throw. The exception is unchecked, as it should not occur
 * in a normal execution flow. To prevent it from occurring in a multi-threaded environment, all additional threads
 * should be stopped or stop using the instance before one thread invokes [[ToxCore.close]] on it, or appropriate
 * exception handlers should be installed in all threads.
 */
trait ToxCore[ToxCoreState] extends Closeable {

  /**
   * Store all information associated with the tox instance to a byte array.
   *
   * The data in the byte array can be used to create a new instance with [[load]] by passing it to the
   * [[ToxOptions]] constructor. The concrete format in this serialised instance is implementation-defined. Passing
   * save data created by one class to a different class may not work.
   *
   * @return a byte array containing the serialised tox instance.
   */
  @NotNull
  def getSaveData: Array[Byte]

  /**
   * Create a new [[ToxCore]] instance with different options. The implementation may choose to create an object of
   * its own class or a different class. If the implementation was compatible with another subsystem implementation (e.g.
   * [[im.tox.tox4j.av.ToxAv]]), then the new object must be compatible with the same implementation.
   *
   * This function will bring the instance into a valid state. Running the event
   * loop with a new instance will operate correctly.
   *
   * If the [[ToxOptions.saveData]] field is not empty, this function will load the Tox instance
   * from a byte array previously filled by [[getSaveData]].
   *
   * If loading failed or succeeded only partially, an exception will be thrown.
   *
   * @return a new [[ToxCore]] instance.
   */
  @NotNull
  @throws[ToxNewException]
  def load(@NotNull options: ToxOptions): ToxCore[ToxCoreState]

  /**
   * Shut down the tox instance.
   *
   * Releases all resources associated with the Tox instance and disconnects from
   * the network.
   *
   * Once this method has been called, all other calls on this instance will throw
   * [[im.tox.tox4j.exceptions.ToxKilledException]]. A closed instance cannot be reused; a new instance must be
   * created.
   */
  override def close(): Unit

  /**
   * Bootstrap into the tox network.
   *
   * Sends a "get nodes" request to the given bootstrap node with IP, port, and
   * public key to setup connections.
   *
   * This function will only attempt to connect to the node using UDP. If you want
   * to additionally attempt to connect using TCP, use [[addTcpRelay]] together with
   * this function.
   *
   * @param address   the hostname, or an IPv4/IPv6 address of the node.
   * @param port      the port of the node.
   * @param publicKey the public key of the node.
   */
  @throws[ToxBootstrapException]
  def bootstrap(@NotNull address: String, port: Int, @NotNull publicKey: Array[Byte]): Unit

  /**
   * Connect to a TCP relay to forward traffic.
   *
   * This function can be used to initiate TCP connections to different ports on
   * the same bootstrap node, or to add TCP relays without using them as
   * bootstrap nodes.
   *
   * @param address   the hostname, or an IPv4/IPv6 address of the node.
   * @param port      the TCP port the node is running a relay on.
   * @param publicKey the public key of the node.
   */
  @throws[ToxBootstrapException]
  def addTcpRelay(@NotNull address: String, port: Int, @NotNull publicKey: Array[Byte]): Unit

  /**
   * Get the UDP port this instance is bound to.
   *
   * @return a port number between 1 and 65535.
   */
  @throws[ToxGetPortException]
  def getUdpPort: Int

  /**
   * Return the TCP port this Tox instance is bound to. This is only relevant if
   * the instance is acting as a TCP relay.
   *
   * @return a port number between 1 and 65535.
   */
  @throws[ToxGetPortException]
  def getTcpPort: Int

  /**
   * Writes the temporary DHT public key of this instance to a byte array.
   *
   * This can be used in combination with an externally accessible IP address and
   * the bound port (from [[getUdpPort]]}) to run a temporary bootstrap node.
   *
   * Be aware that every time a new instance is created, the DHT public key
   * changes, meaning this cannot be used to run a permanent bootstrap node.
   *
   * @return a byte array of size [[ToxCoreConstants.PUBLIC_KEY_SIZE]]
   */
  @NotNull
  def getDhtId: Array[Byte]

  /**
   * Get the time in milliseconds until [[iterate]] should be called again for optimal performance.
   *
   * @return the time in milliseconds until [[iterate]] should be called again.
   */
  def iterationInterval: Int

  /**
   * The main loop.
   *
   * This should be invoked every [[iterationInterval]] milliseconds.
   */
  def iterate(state: ToxCoreState): ToxCoreState

  /**
   * Copy the Tox Public Key (long term) from the Tox object.
   * @return a byte array of size [[ToxCoreConstants.PUBLIC_KEY_SIZE]]
   */
  @NotNull
  def getPublicKey: Array[Byte]

  /**
   * Copy the Tox Secret Key from the Tox object.
   * @return a byte array of size [[ToxCoreConstants.SECRET_KEY_SIZE]]
   */
  @NotNull
  def getSecretKey: Array[Byte]

  /**
   * Set the 4-byte noSpam part of the address.
   *
   * Setting the noSpam makes it impossible for others to send us friend requests that contained the
   * old noSpam number.
   *
   * @param noSpam the new noSpam number.
   */
  def setNoSpam(noSpam: Int): Unit

  /**
   * Get our current noSpam number.
   */
  def getNoSpam: Int

  /**
   * Get our current tox address to give to friends.
   *
   * The format is the following: [Public Key (32 bytes)][noSpam number (4 bytes)][checksum (2 bytes)].
   * After a call to [[setNoSpam]], the old address can no longer be used to send friend requests to
   * this instance.
   *
   * Note that it is not in a human-readable format. To display it to users, it needs to be formatted.
   *
   * @return a byte array of size [[ToxCoreConstants.TOX_ADDRESS_SIZE]]
   */
  @NotNull
  def getAddress: Array[Byte]

  /**
   * Set the nickname for the Tox client.
   *
   * Cannot be longer than [[ToxCoreConstants.MAX_NAME_LENGTH]] bytes. Can be empty (zero-length).
   *
   * @param name A byte array containing the new nickname..
   */
  @throws[ToxSetInfoException]
  def setName(@NotNull name: Array[Byte]): Unit

  /**
   * Get our own nickname.
   */
  @NotNull
  def getName: Array[Byte]

  /**
   * Set our status message.
   *
   * Cannot be longer than [[ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH]] bytes.
   *
   * @param message the status message to set.
   */
  @throws[ToxSetInfoException]
  def setStatusMessage(@NotNull message: Array[Byte]): Unit

  /**
   * Gets our own status message. May be null if the status message was empty.
   */
  @NotNull
  def getStatusMessage: Array[Byte]

  /**
   * Set our status.
   *
   * @param status status to set.
   */
  def setStatus(@NotNull status: ToxUserStatus): Unit

  /**
   * Get our status.
   */
  @NotNull
  def getStatus: ToxUserStatus

  /**
   * Add a friend to the friend list and send a friend request.
   *
   * A friend request message must be at least 1 byte long and at most
   * [[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH]].
   *
   * Friend numbers are unique identifiers used in all functions that operate on
   * friends. Once added, a friend number is stable for the lifetime of the Tox
   * object. After saving the state and reloading it, the friend numbers may not
   * be the same as before. Deleting a friend creates a gap in the friend number
   * set, which is filled by the next adding of a friend. Any pattern in friend
   * numbers should not be relied on.
   *
   * If more than [[Integer.MAX_VALUE]] friends are added, this function throws
   * an exception.
   *
   * @param address the address to add as a friend ([[ToxCoreConstants.TOX_ADDRESS_SIZE]] bytes).
   *                This is the byte array the friend got from their own [[getAddress]].
   * @param message the message to send with the friend request (must not be empty).
   * @return the new friend's friend number.
   */
  @throws[ToxFriendAddException]
  @throws[IllegalArgumentException]("if the Friend Address was not the right length.")
  def addFriend(@NotNull address: Array[Byte], @NotNull message: Array[Byte]): Int

  /**
   * Add a friend without sending a friend request.
   *
   * This function is used to add a friend in response to a friend request. If the
   * client receives a friend request, it can be reasonably sure that the other
   * client added this client as a friend, eliminating the need for a friend
   * request.
   *
   * This function is also useful in a situation where both instances are
   * controlled by the same entity, so that this entity can perform the mutual
   * friend adding. In this case, there is no need for a friend request, either.
   *
   * @param publicKey the Public Key to add as a friend ([[ToxCoreConstants.PUBLIC_KEY_SIZE]] bytes).
   * @return the new friend's friend number.
   */
  @throws[ToxFriendAddException]
  @throws[IllegalArgumentException]("if the Public Key was not the right length.")
  def addFriendNoRequest(@NotNull publicKey: Array[Byte]): Int

  /**
   * Remove a friend from the friend list.
   *
   * This does not notify the friend of their deletion. After calling this
   * function, this client will appear offline to the friend and no communication
   * can occur between the two.
   *
   * @param friendNumber the friend number to delete.
   */
  @throws[ToxFriendDeleteException]
  def deleteFriend(friendNumber: Int): Unit

  /**
   * Gets the friend number for the specified Public Key.
   *
   * @param publicKey the Public Key.
   * @return the friend number that is associated with the Public Key.
   */
  @throws[ToxFriendByPublicKeyException]
  def getFriendByPublicKey(@NotNull publicKey: Array[Byte]): Int

  /**
   * Gets the Public Key for the specified friend number.
   *
   * @param friendNumber the friend number.
   * @return the Public Key associated with the friend number.
   */
  @NotNull
  @throws[ToxFriendGetPublicKeyException]
  def getFriendPublicKey(friendNumber: Int): Array[Byte]

  /**
   * Checks whether a friend with the specified friend number exists.
   *
   * If this function returns <code>true</code>, the return value is valid until the friend is deleted. If
   * <code>false</code> is returned, the return value is valid until either of [[addFriend]] or
   * [[addFriendNoRequest]] is invoked.
   *
   * @param friendNumber the friend number to check.
   * @return true if such a friend exists.
   */
  def friendExists(friendNumber: Int): Boolean

  /**
   * Get an array of currently valid friend numbers.
   *
   * This list is valid until either of the following is invoked: [[deleteFriend]],
   * [[addFriend]], [[addFriendNoRequest]].
   *
   * @return an array containing the currently valid friend numbers, the empty int array if there are no friends.
   */
  @NotNull
  def getFriendList: Array[Int]

  /**
   * Tell friend number whether or not we are currently typing.
   *
   * The client is responsible for turning it on or off.
   *
   * @param friendNumber the friend number to set typing status for.
   * @param typing       <code>true</code> if we are currently typing.
   */
  @throws[ToxSetTypingException]
  def setTyping(friendNumber: Int, typing: Boolean): Unit

  /**
   * Send a text chat message to an online friend.
   *
   * This function creates a chat message packet and pushes it into the send
   * queue.
   *
   * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]].
   * Larger messages must be split by the client and sent as separate messages.
   * Other clients can then reassemble the fragments. Messages may not be empty.
   *
   * The return value of this function is the message ID. If a read receipt is
   * received, the triggered [[FriendReadReceiptCallback]] event will be passed this message ID.
   *
   * Message IDs are unique per friend per instance. The first message ID is 0. Message IDs
   * are incremented by 1 each time a message is sent. If [[Integer.MAX_VALUE]] messages were
   * sent, the next message ID is [[Integer.MIN_VALUE]].
   *
   * Message IDs are not stored in the array returned by [[getSaveData]].
   *
   * @param friendNumber The friend number of the friend to send the message to.
   * @param messageType Message type (normal, action, ...).
   * @param timeDelta The time between composition (user created the message) and calling this function.
   * @param message The message text
   * @return the message ID.
   */
  @throws[ToxFriendSendMessageException]
  def sendMessage(friendNumber: Int, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Int

  /**
   * Sends a file control command to a friend for a given file transfer.
   *
   * @param friendNumber The friend number of the friend the file is being transferred to or received from.
   * @param fileNumber The friend-specific identifier for the file transfer.
   * @param control The control command to send.
   */
  @throws[ToxFileControlException]
  def fileControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl): Unit

  /**
   * Sends a file seek control command to a friend for a given file transfer.
   *
   * This function can only be called to resume a file transfer right before
   * [[ToxFileControl.RESUME]] is sent.
   *
   * @param friendNumber The friend number of the friend the file is being received from.
   * @param fileNumber The friend-specific identifier for the file transfer.
   * @param position The position that the file should be seeked to.
   */
  @throws[ToxFileSeekException]
  def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Unit

  /**
   * Return the file id associated to the file transfer as a byte array.
   *
   * @param friendNumber The friend number of the friend the file is being transferred to or received from.
   * @param fileNumber The friend-specific identifier for the file transfer.
   */
  @throws[ToxFileGetException]
  def fileGetFileId(friendNumber: Int, fileNumber: Int): Array[Byte]

  /**
   * Send a file transmission request.
   *
   * Maximum filename length is [[ToxCoreConstants.MAX_FILENAME_LENGTH]] bytes. The filename
   * should generally just be a file name, not a path with directory names.
   *
   * If a non-negative file size is provided, it can be used by both sides to
   * determine the sending progress. File size can be set to a negative value for streaming
   * data of unknown size.
   *
   * File transmission occurs in chunks, which are requested through the
   * [[FileChunkRequestCallback]] event.
   *
   * When a friend goes offline, all file transfers associated with the friend are
   * purged from core.
   *
   * If the file contents change during a transfer, the behaviour is unspecified
   * in general. What will actually happen depends on the mode in which the file
   * was modified and how the client determines the file size.
   *
   * - If the file size was increased
   *   - and sending mode was streaming (fileSize = -1), the behaviour
   *     will be as expected.
   *   - and sending mode was file (fileSize != -1), the
   *     [[FileChunkRequestCallback]] callback will receive length = 0 when Core thinks
   *     the file transfer has finished. If the client remembers the file size as
   *     it was when sending the request, it will terminate the transfer normally.
   *     If the client re-reads the size, it will think the friend cancelled the
   *     transfer.
   * - If the file size was decreased
   *   - and sending mode was streaming, the behaviour is as expected.
   *   - and sending mode was file, the callback will return 0 at the new
   *     (earlier) end-of-file, signalling to the friend that the transfer was
   *     cancelled.
   * - If the file contents were modified
   *   - at a position before the current read, the two files (local and remote)
   *     will differ after the transfer terminates.
   *   - at a position after the current read, the file transfer will succeed as
   *     expected.
   *   - In either case, both sides will regard the transfer as complete and
   *     successful.
   *
   * @param friendNumber The friend number of the friend the file send request should be sent to.
   * @param kind The meaning of the file to be sent.
   * @param fileSize Size in bytes of the file the client wants to send, -1 if unknown or streaming.
   * @param fileId A file identifier of length [[ToxCoreConstants.FILE_ID_LENGTH]] that can be used to
   *               uniquely identify file transfers across core restarts. If empty, a random one will
   *               be generated by core. It can then be obtained by using [[fileGetFileId]]
   * @param filename Name of the file. Does not need to be the actual name. This
   *                 name will be sent along with the file send request.
   * @return A file number used as an identifier in subsequent callbacks. This
   *         number is per friend. File numbers are reused after a transfer terminates.
   *         Any pattern in file numbers should not be relied on.
   */
  @throws[ToxFileSendException]
  def fileSend(friendNumber: Int, kind: Int, fileSize: Long, @NotNull fileId: Array[Byte], @NotNull filename: Array[Byte]): Int

  /**
   * Send a chunk of file data to a friend.
   *
   * This function is called in response to the [[FileChunkRequestCallback]] callback. The
   * length parameter should be equal to the one received though the callback.
   * If it is zero, the transfer is assumed complete. For files with known size,
   * Core will know that the transfer is complete after the last byte has been
   * received, so it is not necessary (though not harmful) to send a zero-length
   * chunk to terminate. For streams, core will know that the transfer is finished
   * if a chunk with length less than the length requested in the callback is sent.
   *
   * @param friendNumber The friend number of the receiving friend for this file.
   * @param fileNumber The file transfer identifier returned by [[fileSend]].
   * @param position The file or stream position from which the friend should continue writing.
   * @param data The chunk data.
   */
  @throws[ToxFileSendChunkException]
  def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit

  /**
   * Creates a new group chat.
   *
   * This function creates a new group chat object adds it to the chats array.
   *
   * @param privacyState The privacy state of the group. If this is set to [[ToxGroupPrivacyState.PUBLIC]],
   *   the group will attempt to announce itself to the DHT and anyone with the Chat ID may join.
   *   Otherwise a friend invite will be required to join the group.
   * @param groupName The name of the group.
   *
   * @return groupNumber
   */
  @throws[ToxGroupNewException]
  def groupNew(privacyState: ToxGroupPrivacyState, @NotNull groupName: Array[Byte]): Int

  /**
   * Joins a group chat with specified Chat ID.
   *
   * This function creates a new group chat object, adds it to the chats array, and sends
   * a DHT announcement to find peers in the group associated with chatId. Once a peer has been
   * found a join attempt will be initiated.
   *
   * @param chatId The Chat ID of the group you wish to join. This must be [[ToxCoreConstants.GROUP_CHAT_ID_SIZE]] bytes.
   * @param password The password required to join the group. Set to null if no password is required.
   *
   * @return groupNumber on success
   */
  @throws[ToxGroupJoinException]
  def groupJoin(@NotNull chatId: Array[Byte], @Nullable password: Array[Byte]): Int

  /**
   * Reconnects to a group.
   *
   * This function disconnects from all peers in the group, then attempts to reconnect with the group.
   * The caller's state is not changed (i.e. name, status, role, chat public key etc.)
   *
   * @param groupNumber The group number of the group we wish to reconnect to.
   */
  @throws[ToxGroupReconnectException]
  def groupReconnect(groupNumber: Int): Unit

  /**
   * Leaves a group.
   *
   * This function sends a parting packet containing a custom (non-obligatory) message to all
   * peers in a group, and deletes the group from the chat array. All group state information is permanently
   * lost, including keys and role credentials.
   *
   * @param groupNumber The group number of the group we wish to leave.
   * @param message The parting message to be sent to all the peers. Set to null if we do not wish to
   *   send a parting message.
   */
  @throws[ToxGroupLeaveException]
  def groupLeave(groupNumber: Int, @Nullable message: Array[Byte]): Unit

  /**
   * Set the client's nickname for the group instance designated by the given group number.
   *
   * Nickname length cannot exceed [[ToxCoreConstants.MAX_NAME_LENGTH]].
   *
   * @param name A byte array containing the new nickname.
   */
  @throws[ToxGroupSelfNameSetException]
  def setGroupSelfName(groupNumber: Int, name: Array[Byte]): Unit

  /**
   * Get the client's nickname for the group instance designated by the given group number.
   *
   * If no nickname was set before calling this function, the name is empty,
   * and this function has no effect.
   *
   * @param groupNumber The group number of the group to get the nickname from.
   *
   * @return the client's nickname for the group
   */
  @throws[ToxGroupSelfQueryException]
  def getGroupSelfName(groupNumber: Int): Array[Byte]

  /**
   * Set the client's status for the group instance. Status must be a [[ToxUserStatus]].
   */
  @throws[ToxGroupSelfStatusSetException]
  def setGroupSelfStatus(groupNumber: Int, status: ToxUserStatus): Unit

  /**
   * Get the client's status for the group instance.
   *
   * @return the client's status
   */
  @throws[ToxGroupSelfQueryException]
  def getGroupSelfStatus(groupNumber: Int): ToxUserStatus

  /**
   * Get the client's role for the group instance.
   */
  @throws[ToxGroupSelfQueryException]
  def getGroupSelfRole(groupNumber: Int): ToxGroupRole

  /**
   * Write the name of the peer designated by the given peer number to a byte
   * array.
   *
   * The data returned is equal to the data received by the last
   * [[GroupPeerNameCallback]] callback.
   *
   * @param groupNumber The group number of the group we wish to query.
   * @param peerNumber The peer number of the peer whose name we want to retrieve.
   */
  @throws[ToxGroupPeerQueryException]
  def getGroupPeerName(groupNumber: Int, peerNumber: Int): Array[Byte]

  /**
   * Get the peer's user status (away/busy/...)
   *
   * The status returned is equal to the last status received through the
   * [[GroupPeerStatusCallback]] callback.
   */
  @throws[ToxGroupPeerQueryException]
  def getGroupPeerStatus(groupNumber: Int, peerNumber: Int): ToxUserStatus

  /**
   * Get the peer's role (user/moderator/founder...).
   *
   * The role returned is equal to the last role received through the
   * [[GroupModerationCallback]] callback.
   */
  @throws[ToxGroupPeerQueryException]
  def getGroupPeerRole(groupNumber: Int, peerNumber: Int): ToxGroupRole

  /**
   * Set the group topic and broadcast it to the rest of the group.
   *
   * topic length cannot be longer than [[ToxCoreConstants.GROUP_MAX_TOPIC_LENGTH]]. If length is equal to zero or
   * topic is set to null, the topic will be unset.
   */
  @throws[ToxGroupTopicSetException]
  def setGroupTopic(groupNumber: Int, @Nullable topic: Array[Byte]): Unit

  /**
   * Get the topic designated by the given group number.
   *
   * The data returned is equal to the data received by the last
   * [[GroupTopicCallback]] callback.
   *
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupTopic(groupNumber: Int): Array[Byte]

  /**
   * Get the name of the group designated by the given group number.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupName(groupNumber: Int): Array[Byte]

  /**
   * Get the Chat ID designated by the given group number.
   *
   * @return a byte array of size [[ToxCoreConstants.GROUP_CHAT_ID_SIZE]] bytes.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupChatId(groupNumber: Int): Array[Byte]

  /**
   * Get the number of peers in the group designated by the given group number.
   *
   * @see peerNumbers for further information on the implications of the return value.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupNumberPeers(groupNumber: Int): Int

  /**
   * Get the number of groups in the Tox chats array.
   */
  def getGroupNumberGroups: Int

  /**
   * Get the privacy state of the group designated by the given group number.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupPrivacyState(groupNumber: Int): ToxGroupPrivacyState

  /**
   * Get the maximum number of peers allowed for the group designated by the given group number.
   *
   * The value returned is equal to the data received by the last
   * [[GroupPeerLimitCallback]] callback.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupPeerLimit(groupNumber: Int): Int

  /**
   * Get the password for the group designated by the given group number.
   *
   * The data received is equal to the data received by the last
   * [[GroupPasswordCallback]] callback.
   */
  @throws[ToxGroupStateQueriesException]
  def getGroupPassword(groupNumber: Int): Array[Byte]

  /**
   * Send a text chat message to the entire group.
   *
   * This function creates a group message packet and pushes it into the send
   * queue.
   *
   * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]]. Larger messages
   * must be split by the client and sent as separate messages. Other clients can
   * then reassemble the fragments. Messages may not be empty.
   *
   * @param groupNumber The group number of the group the message is intended for.
   * @param messageType Message type (normal, action, ...).
   * @param message A byte array containing the message text.
   */
  @throws[ToxGroupSendMessageException]
  def groupSendMessage(groupNumber: Int, messageType: ToxMessageType, message: Array[Byte]): Unit

  /**
   * Send a text chat message to the specified peer in the specified group.
   *
   * This function creates a group private message packet and pushes it into the send
   * queue.
   *
   * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]]. Larger messages
   * must be split by the client and sent as separate messages. Other clients can
   * then reassemble the fragments. Messages may not be empty.
   *
   * @param groupNumber The group number of the group the message is intended for.
   * @param peerNumber The peer number of the peer the message is intended for.
   * @param message A byte array containing the message text.
   */
  @throws[ToxGroupSendPrivateMessageException]
  def groupSendPrivateMessage(groupNumber: Int, peerNumber: Int, message: Array[Byte]): Unit

  /**
   * Invite a friend to a group.
   *
   * This function creates an invite request packet and pushes it to the send queue.
   *
   * @param groupNumber The group number of the group the message is intended for.
   * @param friendNumber The friend number of the friend the invite is intended for.
   */
  @throws[ToxGroupInviteFriendException]
  def groupInviteFriend(groupNumber: Int, friendNumber: Int): Unit

  /**
   * Accept an invite to a group chat that the client previously received from a friend. The invite
   * is only valid while the inviter is present in the group.
   *
   * @param inviteData The invite data received from the [[GroupInviteCallback]] callback.
   * @param password The password required to join the group. Set to null if no password is required.
   *                 Must be no larger than [[ToxCoreConstants.GROUP_MAX_PASSWORD_SIZE]].
   *
   * @return the group number
   */
  @throws[ToxGroupInviteAcceptException]
  def groupInviteAccept(@NotNull inviteData: Array[Byte], @Nullable password: Array[Byte]): Int

  /**
   * Set or unset the group password.
   *
   * This function sets the groups password, creates a new group shared state including the change,
   * and distributes it to the rest of the group.
   *
   * @param groupNumber The group number of the group for which we wish to set the password.
   * @param password The password we want to set. Set password to null to unset the password.
   *                 Must be no longer than [[ToxCoreConstants.GROUP_MAX_PASSWORD_SIZE]].
   */
  @throws[ToxGroupFounderSetPasswordException]
  def setGroupFounderPassword(groupNumber: Int, @Nullable password: Array[Byte]): Unit

  /**
   * Set the group privacy state.
   *
   * This function sets the group's privacy state, creates a new group shared state
   * including the change, and distributes it to the rest of the group.
   *
   * If an attempt is made to set the privacy state to the same state that the group is already
   * in, the function call will be successful and no action will be taken.
   *
   * @param groupNumber The group number of the group for which we wish to change the privacy state.
   * @param privacyState The privacy state we wish to set the group to.
   */
  @throws[ToxGroupFounderSetPrivacyStateException]
  def setGroupFounderPrivacyState(groupNumber: Int, privacyState: ToxGroupPrivacyState): Unit

  /**
   * Set the group peer limit.
   *
   * This function sets a limit for the number of peers who may be in the group, creates a new
   * group shared state including the change, and distributes it to the rest of the group.
   *
   * @param groupNumber The group number of the group for which we wish to set the peer limit.
   * @param maxPeers The maximum number of peers to allow in the group.
   */
  @throws[ToxGroupFounderSetPeerLimitException]
  def setGroupFounderPeerLimit(groupNumber: Int, maxPeers: Int): Unit

  /**
   * Ignore or unignore a peer.
   *
   * @param groupNumber The group number of the group the in which you wish to ignore a peer.
   * @param peerNumber The peer number of the peer who shall be ignored or unignored.
   * @param ignore True to ignore the peer, false to unignore the peer.
   */
  @throws[ToxGroupToggleIgnoreException]
  def groupToggleIgnore(groupNumber: Int, peerNumber: Int, ignore: Boolean): Unit

  /**
   * Set a peer's role.
   *
   * This function will first remove the peer's previous role and then assign them a new role.
   * It will also send a packet to the rest of the group, requesting that they perform
   * the role reassignment. Note: peers cannot be set to the founder role.
   *
   * @param groupNumber The group number of the group the in which you wish set the peer's role.
   * @param peerNumber The peer number of the peer whose role you wish to set.
   * @param role The role you wish to set the peer to.
   */
  @throws[ToxGroupModSetRoleException]
  def setGroupModRole(groupNumber: Int, peerNumber: Int, role: ToxGroupRole): Unit

  /**
   * Kick/ban a peer.
   *
   * This function will remove a peer from the caller's peer list and optionally add their IP address
   * to the ban list. It will also send a packet to all group members requesting them
   * to do the same.
   *
   * @param groupNumber The group number of the group the ban is intended for.
   * @param peerNumber The peer number of the peer who will be kicked and/or added to the ban list.
   * @param setBan Set to true if a ban shall be set on the peer's IP address.
   */
  @throws[ToxGroupModRemovePeerException]
  def groupModRemovePeer(groupNumber: Int, peerNumber: Int, setBan: Boolean): Unit

  /**
   * Removes a ban.
   *
   * This function removes a ban entry from the ban list, and sends a packet to the rest of
   * the group requesting that they do the same.
   *
   * @param groupNumber The group number of the group in which the ban is to be removed.
   * @param banId The ID of the ban entry that shall be removed.
   */
  @throws[ToxGroupModRemoveBanException]
  def groupModRemoveBan(groupNumber: Int, banId: Short): Unit

  /**
   * Get a list of valid ban list ID's.
   *
   * @return a list of ban ID's
   */
  @throws[ToxGroupBanQueryException]
  def getGroupBanList(groupNumber: Int): Array[Short]

  /**
   * Get the name of the ban entry designated by banId in the group designated by the
   * given group number as a byte array.
   *
   * @return the name
   */
  @throws[ToxGroupBanQueryException]
  def getGroupBanName(groupNumber: Int, banId: Short): Array[Byte]

  /**
   * Return a time stamp indicating the time the ban was set, for the ban list entry
   * designated by banId, in the group designated by the given group number.
   */
  @throws[ToxGroupBanQueryException]
  def getGroupBanTimeSet(groupNumber: Int, banId: Short): Long

  /**
   * Send a custom lossy packet to a friend.
   *
   * The first byte of data must be in the range 200-254. Maximum length of a
   * custom packet is [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]].
   *
   * Lossy packets behave like UDP packets, meaning they might never reach the
   * other side or might arrive more than once (if someone is messing with the
   * connection) or might arrive in the wrong order.
   *
   * Unless latency is an issue, it is recommended that you use lossless custom
   * packets instead.
   *
   * @param friendNumber The friend number of the friend this lossy packet should be sent to.
   * @param data A byte array containing the packet data including packet id.
   */
  @throws[ToxFriendCustomPacketException]
  def sendLossyPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit

  /**
   * Send a custom lossless packet to a friend.
   *
   * The first byte of data must be in the range 160-191. Maximum length of a
   * custom packet is [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]].
   *
   * Lossless packet behaviour is comparable to TCP (reliability, arrive in order)
   * but with packets instead of a stream.
   *
   * @param friendNumber The friend number of the friend this lossless packet should be sent to.
   * @param data A byte array containing the packet data including packet id.
   */
  @throws[ToxFriendCustomPacketException]
  def sendLosslessPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit

  /**
   * Register the core event handler.
   *
   * @param handler An event handler capable of handling all Tox events.
   */
  def callback(@NotNull handler: ToxEventListener[ToxCoreState]): Unit

}
