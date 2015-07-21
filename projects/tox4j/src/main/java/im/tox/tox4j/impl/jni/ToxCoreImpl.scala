package im.tox.tox4j.impl.jni

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.ToxImplBase.tryAndLog
import im.tox.tox4j.core.callbacks._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core.proto.Core._
import im.tox.tox4j.core.{ ToxCore, ToxCoreConstants }
import im.tox.tox4j.impl.jni.ToxCoreImpl.{ convert, logger }
import im.tox.tox4j.impl.jni.internal.Event
import org.jetbrains.annotations.{ NotNull, Nullable }
import org.slf4j.LoggerFactory

import scalaz.Scalaz._

// scalastyle:off null
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
private object ToxCoreImpl {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  @throws[ToxBootstrapException]
  private def checkBootstrapArguments(port: Int, @Nullable publicKey: Array[Byte]): Unit = {
    if (port < 0) {
      throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_PORT, "Port cannot be negative")
    }
    if (port > 65535) {
      throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_PORT, "Port cannot exceed 65535")
    }
    if (publicKey ne null) {
      if (publicKey.length < ToxCoreConstants.PUBLIC_KEY_SIZE) {
        throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too short")
      }
      if (publicKey.length > ToxCoreConstants.PUBLIC_KEY_SIZE) {
        throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too long")
      }
    }
  }

  private def convert(status: Connection.Type): ToxConnection = {
    status match {
      case Connection.Type.NONE => ToxConnection.NONE
      case Connection.Type.TCP  => ToxConnection.TCP
      case Connection.Type.UDP  => ToxConnection.UDP
    }
  }

  private def convert(status: UserStatus.Type): ToxUserStatus = {
    status match {
      case UserStatus.Type.NONE => ToxUserStatus.NONE
      case UserStatus.Type.AWAY => ToxUserStatus.AWAY
      case UserStatus.Type.BUSY => ToxUserStatus.BUSY
    }
  }

  private def convert(control: FileControl.Type): ToxFileControl = {
    control match {
      case FileControl.Type.RESUME => ToxFileControl.RESUME
      case FileControl.Type.PAUSE  => ToxFileControl.PAUSE
      case FileControl.Type.CANCEL => ToxFileControl.CANCEL
    }
  }

  private def convert(messageType: MessageType.Type): ToxMessageType = {
    messageType match {
      case MessageType.Type.NORMAL => ToxMessageType.NORMAL
      case MessageType.Type.ACTION => ToxMessageType.ACTION
    }
  }

  private def convert(privacyState: PrivacyState.Type): ToxGroupPrivacyState = {
    privacyState match {
      case PrivacyState.Type.PUBLIC  => ToxGroupPrivacyState.PUBLIC
      case PrivacyState.Type.PRIVATE => ToxGroupPrivacyState.PRIVATE
    }
  }

  private def convert(joinFail: JoinFail.Type): ToxGroupJoinFail = {
    joinFail match {
      case JoinFail.Type.NAME_TAKEN       => ToxGroupJoinFail.NAME_TAKEN
      case JoinFail.Type.PEER_LIMIT       => ToxGroupJoinFail.PEER_LIMIT
      case JoinFail.Type.INVALID_PASSWORD => ToxGroupJoinFail.INVALID_PASSWORD
      case JoinFail.Type.UNKNOWN          => ToxGroupJoinFail.UNKNOWN
    }
  }

  private def convert(groupModEvent: GroupModEvent.Type): ToxGroupModEvent = {
    groupModEvent match {
      case GroupModEvent.Type.KICK      => ToxGroupModEvent.KICK
      case GroupModEvent.Type.BAN       => ToxGroupModEvent.BAN
      case GroupModEvent.Type.OBSERVER  => ToxGroupModEvent.OBSERVER
      case GroupModEvent.Type.USER      => ToxGroupModEvent.USER
      case GroupModEvent.Type.MODERATOR => ToxGroupModEvent.MODERATOR
    }
  }

  private def throwLengthException(name: String, message: String, expectedSize: Int): Unit = {
    throw new IllegalArgumentException(s"$name too $message, must be $expectedSize bytes")
  }

  private def checkLength(name: String, @Nullable bytes: Array[Byte], expectedSize: Int): Unit = {
    if (bytes ne null) {
      if (bytes.length < expectedSize) {
        throwLengthException(name, "short", expectedSize)
      }
      if (bytes.length > expectedSize) {
        throwLengthException(name, "long", expectedSize)
      }
    }
  }

  @throws[ToxSetInfoException]
  private def checkInfoNotNull(info: Array[Byte]): Unit = {
    if (info eq null) {
      throw new ToxSetInfoException(ToxSetInfoException.Code.NULL)
    }
  }

}

/**
 * Initialises the new Tox instance with an optional save-data received from [[getSaveData]].
 *
 * @param options Connection options object with optional save-data.
 */
// scalastyle:off no.finalize number.of.methods
@throws[ToxNewException]("If an error was detected in the configuration or a runtime error occurred.")
final class ToxCoreImpl[ToxCoreState](@NotNull val options: ToxOptions) extends ToxCore[ToxCoreState] {

  private val onCloseCallbacks = new Event

  private var eventListener: ToxEventListener[ToxCoreState] = new ToxEventAdapter // scalastyle:ignore var.field

  /**
   * This field has package visibility for [[ToxAvImpl]].
   */
  private[impl] val instanceNumber =
    ToxCoreJni.toxNew(
      options.ipv6Enabled,
      options.udpEnabled,
      options.proxy.proxyType.ordinal,
      options.proxy.proxyAddress,
      options.proxy.proxyPort,
      options.startPort,
      options.endPort,
      options.tcpPort,
      options.saveData.kind.ordinal,
      options.saveData.data.toArray
    )

  /**
   * Add an onClose callback. This event is invoked just before the instance is closed.
   */
  def addOnCloseCallback(callback: () => Unit): Event.Id =
    onCloseCallbacks += callback

  def removeOnCloseCallback(id: Event.Id): Unit =
    onCloseCallbacks -= id

  override def load(options: ToxOptions): ToxCoreImpl[ToxCoreState] =
    new ToxCoreImpl[ToxCoreState](options)

  override def close(): Unit = {
    onCloseCallbacks()
    ToxCoreJni.toxKill(instanceNumber)
  }

  protected override def finalize(): Unit = {
    try {
      close()
      ToxCoreJni.toxFinalize(instanceNumber)
    } catch {
      case e: Throwable =>
        logger.error("Exception caught in finalizer; this indicates a serious problem in native code", e)
    }
    super.finalize()
  }

  @throws[ToxBootstrapException]
  override def bootstrap(address: String, port: Int, publicKey: Array[Byte]): Unit = {
    ToxCoreImpl.checkBootstrapArguments(port, publicKey)
    ToxCoreJni.toxBootstrap(instanceNumber, address, port, publicKey)
  }

  @throws[ToxBootstrapException]
  override def addTcpRelay(address: String, port: Int, publicKey: Array[Byte]): Unit = {
    ToxCoreImpl.checkBootstrapArguments(port, publicKey)
    ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port, publicKey)
  }

  override def getSaveData: Array[Byte] =
    ToxCoreJni.toxGetSavedata(instanceNumber)

  @throws[ToxGetPortException]
  override def getUdpPort: Int =
    ToxCoreJni.toxGetUdpPort(instanceNumber)

  @throws[ToxGetPortException]
  override def getTcpPort: Int =
    ToxCoreJni.toxGetTcpPort(instanceNumber)

  override def getDhtId: Array[Byte] =
    ToxCoreJni.toxGetDhtId(instanceNumber)

  override def iterationInterval: Int =
    ToxCoreJni.toxIterationInterval(instanceNumber)

  private def dispatchSelfConnectionStatus(selfConnectionStatus: Seq[SelfConnectionStatus])(state: ToxCoreState): ToxCoreState = {
    selfConnectionStatus.foldLeft(state) {
      case (state, SelfConnectionStatus(status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.selfConnectionStatus(
          convert(status)
        ))
    }
  }

  private def dispatchFriendName(friendName: Seq[FriendName])(state: ToxCoreState): ToxCoreState = {
    friendName.foldLeft(state) {
      case (state, FriendName(friendNumber, name)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendName(
          friendNumber,
          name.toByteArray
        ))
    }
  }

  private def dispatchFriendStatusMessage(friendStatusMessage: Seq[FriendStatusMessage])(state: ToxCoreState): ToxCoreState = {
    friendStatusMessage.foldLeft(state) {
      case (state, FriendStatusMessage(friendNumber, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendStatusMessage(
          friendNumber,
          message.toByteArray
        ))
    }
  }

  private def dispatchFriendStatus(friendStatus: Seq[FriendStatus])(state: ToxCoreState): ToxCoreState = {
    friendStatus.foldLeft(state) {
      case (state, FriendStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendStatus(
          friendNumber,
          convert(status)
        ))
    }
  }

  private def dispatchFriendConnectionStatus(friendConnectionStatus: Seq[FriendConnectionStatus])(state: ToxCoreState): ToxCoreState = {
    friendConnectionStatus.foldLeft(state) {
      case (state, FriendConnectionStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendConnectionStatus(
          friendNumber,
          convert(status)
        ))
    }
  }

  private def dispatchFriendTyping(friendTyping: Seq[FriendTyping])(state: ToxCoreState): ToxCoreState = {
    friendTyping.foldLeft(state) {
      case (state, FriendTyping(friendNumber, isTyping)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendTyping(
          friendNumber,
          isTyping
        ))
    }
  }

  private def dispatchFriendReadReceipt(friendReadReceipt: Seq[FriendReadReceipt])(state: ToxCoreState): ToxCoreState = {
    friendReadReceipt.foldLeft(state) {
      case (state, FriendReadReceipt(friendNumber, messageId)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendReadReceipt(
          friendNumber,
          messageId
        ))
    }
  }

  private def dispatchFriendRequest(friendRequest: Seq[FriendRequest])(state: ToxCoreState): ToxCoreState = {
    friendRequest.foldLeft(state) {
      case (state, FriendRequest(publicKey, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendRequest(
          publicKey.toByteArray,
          timeDelta,
          message.toByteArray
        ))
    }
  }

  private def dispatchFriendMessage(friendMessage: Seq[FriendMessage])(state: ToxCoreState): ToxCoreState = {
    friendMessage.foldLeft(state) {
      case (state, FriendMessage(friendNumber, messageType, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendMessage(
          friendNumber,
          convert(messageType),
          timeDelta,
          message.toByteArray
        ))
    }
  }

  private def dispatchFileRecvControl(fileRecvControl: Seq[FileRecvControl])(state: ToxCoreState): ToxCoreState = {
    fileRecvControl.foldLeft(state) {
      case (state, FileRecvControl(friendNumber, fileNumber, control)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecvControl(
          friendNumber,
          fileNumber,
          convert(control)
        ))
    }
  }

  private def dispatchFileChunkRequest(fileChunkRequest: Seq[FileChunkRequest])(state: ToxCoreState): ToxCoreState = {
    fileChunkRequest.foldLeft(state) {
      case (state, FileChunkRequest(friendNumber, fileNumber, position, length)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileChunkRequest(
          friendNumber,
          fileNumber,
          position,
          length
        ))
    }
  }

  private def dispatchFileRecv(fileRecv: Seq[FileRecv])(state: ToxCoreState): ToxCoreState = {
    fileRecv.foldLeft(state) {
      case (state, FileRecv(friendNumber, fileNumber, kind, fileSize, filename)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecv(
          friendNumber,
          fileNumber,
          kind,
          fileSize,
          filename.toByteArray
        ))
    }
  }

  private def dispatchFileRecvChunk(fileRecvChunk: Seq[FileRecvChunk])(state: ToxCoreState): ToxCoreState = {
    fileRecvChunk.foldLeft(state) {
      case (state, FileRecvChunk(friendNumber, fileNumber, position, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecvChunk(
          friendNumber,
          fileNumber,
          position,
          data.toByteArray
        ))
    }
  }

  private def dispatchFriendLossyPacket(friendLossyPacket: Seq[FriendLossyPacket])(state: ToxCoreState): ToxCoreState = {
    friendLossyPacket.foldLeft(state) {
      case (state, FriendLossyPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendLossyPacket(
          friendNumber,
          data.toByteArray
        ))
    }
  }

  private def dispatchFriendLosslessPacket(friendLosslessPacket: Seq[FriendLosslessPacket])(state: ToxCoreState): ToxCoreState = {
    friendLosslessPacket.foldLeft(state) {
      case (state, FriendLosslessPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendLosslessPacket(
          friendNumber,
          data.toByteArray
        ))
    }
  }

  private def dispatchGroupPeerName(groupPeerName: Seq[GroupPeerName])(state: ToxCoreState): ToxCoreState = {
    groupPeerName.foldLeft(state) {
      case (state, GroupPeerName(groupNumber, peerNumber, name)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerName(
          groupNumber, peerNumber, name.toByteArray
        ))
    }
  }

  private def dispatchGroupPeerStatus(groupPeerStatus: Seq[GroupPeerStatus])(state: ToxCoreState): ToxCoreState = {
    groupPeerStatus.foldLeft(state) {
      case (state, GroupPeerStatus(groupNumber, peerNumber, status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerStatus(
          groupNumber, peerNumber, convert(status)
        ))
    }
  }

  private def dispatchGroupTopic(groupTopic: Seq[GroupTopic])(state: ToxCoreState): ToxCoreState = {
    groupTopic.foldLeft(state) {
      case (state, GroupTopic(groupNumber, peerNumber, topic)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupTopic(
          groupNumber, peerNumber, topic.toByteArray
        ))
    }
  }

  private def dispatchGroupPrivacyState(groupPrivacyState: Seq[GroupPrivacyState])(state: ToxCoreState): ToxCoreState = {
    groupPrivacyState.foldLeft(state) {
      case (state, GroupPrivacyState(groupNumber, privacyState)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPrivacyState(
          groupNumber, convert(privacyState)
        ))
    }
  }

  private def dispatchGroupPeerLimit(groupPeerLimit: Seq[GroupPeerLimit])(state: ToxCoreState): ToxCoreState = {
    groupPeerLimit.foldLeft(state) {
      case (state, GroupPeerLimit(groupNumber, peerLimit)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerLimit(
          groupNumber, peerLimit
        ))
    }
  }

  private def dispatchGroupPassword(groupPassword: Seq[GroupPassword])(state: ToxCoreState): ToxCoreState = {
    groupPassword.foldLeft(state) {
      case (state, GroupPassword(groupNumber, password)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPassword(
          groupNumber, password.toByteArray
        ))
    }
  }

  private def dispatchGroupPeerlistUpdate(groupPeerlistUpdate: Seq[GroupPeerlistUpdate])(state: ToxCoreState): ToxCoreState = {
    groupPeerlistUpdate.foldLeft(state) {
      case (state, GroupPeerlistUpdate(groupNumber)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerlistUpdate(
          groupNumber
        ))
    }
  }

  private def dispatchGroupMessage(groupMessage: Seq[GroupMessage])(state: ToxCoreState): ToxCoreState = {
    groupMessage.foldLeft(state) {
      case (state, GroupMessage(groupNumber, peerNumber, messageType, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupMessage(
          groupNumber, peerNumber, convert(messageType), message.toByteArray
        ))
    }
  }

  private def dispatchGroupPrivateMessage(groupPrivateMessage: Seq[GroupPrivateMessage])(state: ToxCoreState): ToxCoreState = {
    groupPrivateMessage.foldLeft(state) {
      case (state, GroupPrivateMessage(groupNumber, peerNumber, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPrivateMessage(
          groupNumber, peerNumber, message.toByteArray
        ))
    }
  }

  private def dispatchGroupInvite(groupInvite: Seq[GroupInvite])(state: ToxCoreState): ToxCoreState = {
    groupInvite.foldLeft(state) {
      case (state, GroupInvite(friendNumber, inviteData)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupInvite(
          friendNumber, inviteData.toByteArray
        ))
    }
  }

  private def dispatchGroupPeerJoin(groupPeerJoin: Seq[GroupPeerJoin])(state: ToxCoreState): ToxCoreState = {
    groupPeerJoin.foldLeft(state) {
      case (state, GroupPeerJoin(groupNumber, peerNumber)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerJoin(
          groupNumber, peerNumber
        ))
    }
  }

  private def dispatchGroupPeerExit(groupPeerExit: Seq[GroupPeerExit])(state: ToxCoreState): ToxCoreState = {
    groupPeerExit.foldLeft(state) {
      case (state, GroupPeerExit(groupNumber, peerNumber, partMessage)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupPeerExit(
          groupNumber, peerNumber, partMessage.toByteArray
        ))
    }
  }

  private def dispatchGroupSelfJoin(groupSelfJoin: Seq[GroupSelfJoin])(state: ToxCoreState): ToxCoreState = {
    groupSelfJoin.foldLeft(state) {
      case (state, GroupSelfJoin(groupNumber)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupSelfJoin(
          groupNumber
        ))
    }
  }

  private def dispatchGroupJoinFail(groupJoinFail: Seq[GroupJoinFail])(state: ToxCoreState): ToxCoreState = {
    groupJoinFail.foldLeft(state) {
      case (state, GroupJoinFail(groupNumber, groupJoinFailType)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupJoinFail(
          groupNumber, convert(groupJoinFailType)
        ))
    }
  }

  private def dispatchGroupModeration(groupModeration: Seq[GroupModeration])(state: ToxCoreState): ToxCoreState = {
    groupModeration.foldLeft(state) {
      case (state, GroupModeration(groupNumber, sourcePeerNumber, targetPeerNumber, groupModEventType)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.groupModeration(
          groupNumber, sourcePeerNumber, targetPeerNumber, convert(groupModEventType)
        ))
    }
  }

  private def dispatchEvents(state: ToxCoreState, events: CoreEvents): ToxCoreState = {
    (state
      |> dispatchSelfConnectionStatus(events.selfConnectionStatus)
      |> dispatchFriendName(events.friendName)
      |> dispatchFriendStatusMessage(events.friendStatusMessage)
      |> dispatchFriendStatus(events.friendStatus)
      |> dispatchFriendConnectionStatus(events.friendConnectionStatus)
      |> dispatchFriendTyping(events.friendTyping)
      |> dispatchFriendReadReceipt(events.friendReadReceipt)
      |> dispatchFriendRequest(events.friendRequest)
      |> dispatchFriendMessage(events.friendMessage)
      |> dispatchFileRecvControl(events.fileRecvControl)
      |> dispatchFileChunkRequest(events.fileChunkRequest)
      |> dispatchFileRecv(events.fileRecv)
      |> dispatchFileRecvChunk(events.fileRecvChunk)
      |> dispatchFriendLossyPacket(events.friendLossyPacket)
      |> dispatchFriendLosslessPacket(events.friendLosslessPacket)
      |> dispatchGroupPeerName(events.groupPeerName)
      |> dispatchGroupPeerStatus(events.groupPeerStatus)
      |> dispatchGroupTopic(events.groupTopic)
      |> dispatchGroupPrivacyState(events.groupPrivacyState)
      |> dispatchGroupPeerLimit(events.groupPeerLimit)
      |> dispatchGroupPassword(events.groupPassword)
      |> dispatchGroupPeerlistUpdate(events.groupPeerlistUpdate)
      |> dispatchGroupMessage(events.groupMessage)
      |> dispatchGroupPrivateMessage(events.groupPrivateMessage)
      |> dispatchGroupInvite(events.groupInvite)
      |> dispatchGroupPeerJoin(events.groupPeerJoin)
      |> dispatchGroupPeerExit(events.groupPeerExit)
      |> dispatchGroupSelfJoin(events.groupSelfJoin)
      |> dispatchGroupJoinFail(events.groupJoinFail)
      |> dispatchGroupModeration(events.groupModeration))
  }

  override def iterate(state: ToxCoreState): ToxCoreState = {
    Option(ToxCoreJni.toxIterate(instanceNumber))
      .map(CoreEvents.parseFrom)
      .foldLeft(state)(dispatchEvents)
  }

  override def getPublicKey: Array[Byte] =
    ToxCoreJni.toxSelfGetPublicKey(instanceNumber)

  override def getSecretKey: Array[Byte] =
    ToxCoreJni.toxSelfGetSecretKey(instanceNumber)

  override def setNoSpam(nospam: Int): Unit =
    ToxCoreJni.toxSelfSetNospam(instanceNumber, nospam)

  override def getNoSpam: Int =
    ToxCoreJni.toxSelfGetNospam(instanceNumber)

  override def getAddress: Array[Byte] =
    ToxCoreJni.toxSelfGetAddress(instanceNumber)

  @throws[ToxSetInfoException]
  override def setName(name: Array[Byte]): Unit = {
    ToxCoreImpl.checkInfoNotNull(name)
    ToxCoreJni.toxSelfSetName(instanceNumber, name)
  }

  override def getName: Array[Byte] = ToxCoreJni.toxSelfGetName(instanceNumber)

  @throws[ToxSetInfoException]
  override def setStatusMessage(message: Array[Byte]): Unit = {
    ToxCoreImpl.checkInfoNotNull(message)
    ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, message)
  }

  override def getStatusMessage: Array[Byte] =
    ToxCoreJni.toxSelfGetStatusMessage(instanceNumber)

  override def setStatus(status: ToxUserStatus): Unit =
    ToxCoreJni.toxSelfSetStatus(instanceNumber, status.ordinal)

  override def getStatus: ToxUserStatus =
    ToxUserStatus.values()(ToxCoreJni.toxSelfGetStatus(instanceNumber))

  @throws[ToxFriendAddException]
  override def addFriend(address: Array[Byte], message: Array[Byte]): Int = {
    ToxCoreImpl.checkLength("Friend Address", address, ToxCoreConstants.TOX_ADDRESS_SIZE)
    ToxCoreJni.toxFriendAdd(instanceNumber, address, message)
  }

  @throws[ToxFriendAddException]
  override def addFriendNoRequest(publicKey: Array[Byte]): Int = {
    ToxCoreImpl.checkLength("Public Key", publicKey, ToxCoreConstants.PUBLIC_KEY_SIZE)
    ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey)
  }

  @throws[ToxFriendDeleteException]
  override def deleteFriend(friendNumber: Int): Unit =
    ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber)

  @throws[ToxFriendByPublicKeyException]
  override def getFriendByPublicKey(publicKey: Array[Byte]): Int =
    ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey)

  @throws[ToxFriendGetPublicKeyException]
  override def getFriendPublicKey(friendNumber: Int): Array[Byte] =
    ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber)

  override def friendExists(friendNumber: Int): Boolean =
    ToxCoreJni.toxFriendExists(instanceNumber, friendNumber)

  override def getFriendList: Array[Int] =
    ToxCoreJni.toxSelfGetFriendList(instanceNumber)

  @throws[ToxSetTypingException]
  override def setTyping(friendNumber: Int, typing: Boolean): Unit =
    ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber, typing)

  @throws[ToxFriendSendMessageException]
  override def sendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: Array[Byte]): Int =
    ToxCoreJni.toxFriendSendMessage(instanceNumber, friendNumber, messageType.ordinal, timeDelta, message)

  @throws[ToxFileControlException]
  override def fileControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl): Unit =
    ToxCoreJni.toxFileControl(instanceNumber, friendNumber, fileNumber, control.ordinal)

  @throws[ToxFileSeekException]
  override def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Unit =
    ToxCoreJni.toxFileSeek(instanceNumber, friendNumber, fileNumber, position)

  @throws[ToxFileSendException]
  override def fileSend(friendNumber: Int, kind: Int, fileSize: Long, @NotNull fileId: Array[Byte], filename: Array[Byte]): Int =
    ToxCoreJni.toxFileSend(instanceNumber, friendNumber, kind, fileSize, fileId, filename)

  @throws[ToxFileSendChunkException]
  override def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]): Unit =
    ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber, fileNumber, position, data)

  @throws[ToxFileGetException]
  override def fileGetFileId(friendNumber: Int, fileNumber: Int): Array[Byte] =
    ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber, fileNumber)

  @throws[ToxGroupNewException]
  override def groupNew(privacyState: ToxGroupPrivacyState, groupName: Array[Byte]): Int =
    ToxCoreJni.toxGroupNew(instanceNumber, privacyState.ordinal, groupName)

  @throws[ToxGroupJoinException]
  override def groupJoin(chatId: Array[Byte], password: Array[Byte]): Int =
    ToxCoreJni.toxGroupJoin(instanceNumber, chatId, password)

  @throws[ToxGroupReconnectException]
  override def groupReconnect(groupNumber: Int): Unit =
    ToxCoreJni.toxGroupReconnect(instanceNumber, groupNumber)

  @throws[ToxGroupLeaveException]
  override def groupLeave(groupNumber: Int, message: Array[Byte]): Unit =
    ToxCoreJni.toxGroupLeave(instanceNumber, groupNumber, message)

  @throws[ToxGroupSelfNameSetException]
  override def setGroupSelfName(groupNumber: Int, name: Array[Byte]): Unit =
    ToxCoreJni.toxGroupSelfSetName(instanceNumber, groupNumber, name)

  @throws[ToxGroupSelfQueryException]
  override def getGroupSelfName(groupNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupSelfGetName(instanceNumber, groupNumber)

  @throws[ToxGroupSelfStatusSetException]
  override def setGroupSelfStatus(groupNumber: Int, status: ToxUserStatus): Unit =
    ToxCoreJni.toxGroupSelfSetStatus(instanceNumber, groupNumber, status.ordinal)

  @throws[ToxGroupSelfQueryException]
  override def getGroupSelfStatus(groupNumber: Int): ToxUserStatus =
    ToxUserStatus.values()(ToxCoreJni.toxGroupSelfGetStatus(instanceNumber, groupNumber))

  @throws[ToxGroupSelfQueryException]
  override def getGroupSelfRole(groupNumber: Int): ToxGroupRole =
    ToxGroupRole.values()(ToxCoreJni.toxGroupSelfGetRole(instanceNumber, groupNumber))

  @throws[ToxGroupPeerQueryException]
  override def getGroupPeerName(groupNumber: Int, peerNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupPeerGetName(instanceNumber, groupNumber, peerNumber)

  @throws[ToxGroupPeerQueryException]
  override def getGroupPeerStatus(groupNumber: Int, peerNumber: Int): ToxUserStatus =
    ToxUserStatus.values()(ToxCoreJni.toxGroupPeerGetStatus(instanceNumber, groupNumber, peerNumber))

  @throws[ToxGroupPeerQueryException]
  override def getGroupPeerRole(groupNumber: Int, peerNumber: Int): ToxGroupRole =
    ToxGroupRole.values()(ToxCoreJni.toxGroupPeerGetRole(instanceNumber, groupNumber, peerNumber))

  @throws[ToxGroupTopicSetException]
  override def setGroupTopic(groupNumber: Int, topic: Array[Byte]): Unit =
    ToxCoreJni.toxGroupSetTopic(instanceNumber, groupNumber, topic)

  @throws[ToxGroupStateQueriesException]
  override def getGroupTopic(groupNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupGetTopic(instanceNumber, groupNumber)

  @throws[ToxGroupStateQueriesException]
  override def getGroupName(groupNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupGetName(instanceNumber, groupNumber)

  @throws[ToxGroupStateQueriesException]
  override def getGroupChatId(groupNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupGetChatId(instanceNumber, groupNumber)

  @throws[ToxGroupStateQueriesException]
  override def getGroupNumberPeers(groupNumber: Int): Int =
    ToxCoreJni.toxGroupGetNumberPeers(instanceNumber, groupNumber)

  override def getGroupNumberGroups: Int =
    ToxCoreJni.toxGroupGetNumberGroups(instanceNumber)

  @throws[ToxGroupStateQueriesException]
  override def getGroupPrivacyState(groupNumber: Int): ToxGroupPrivacyState =
    ToxGroupPrivacyState.values()(ToxCoreJni.toxGroupGetPrivacyState(instanceNumber, groupNumber))

  @throws[ToxGroupStateQueriesException]
  override def getGroupPeerLimit(groupNumber: Int): Int =
    ToxCoreJni.toxGroupGetPeerLimit(instanceNumber, groupNumber)

  @throws[ToxGroupStateQueriesException]
  override def getGroupPassword(groupNumber: Int): Array[Byte] =
    ToxCoreJni.toxGroupGetPassword(instanceNumber, groupNumber)

  @throws[ToxGroupSendMessageException]
  override def groupSendMessage(groupNumber: Int, messageType: ToxMessageType, message: Array[Byte]): Unit =
    ToxCoreJni.toxGroupSendMessage(instanceNumber, groupNumber, messageType.ordinal, message)

  @throws[ToxGroupSendPrivateMessageException]
  override def groupSendPrivateMessage(groupNumber: Int, peerNumber: Int, message: Array[Byte]): Unit =
    ToxCoreJni.toxGroupSendPrivateMessage(instanceNumber, groupNumber, peerNumber, message)

  @throws[ToxGroupInviteFriendException]
  override def groupInviteFriend(groupNumber: Int, friendNumber: Int): Unit =
    ToxCoreJni.toxGroupInviteFriend(instanceNumber, groupNumber, friendNumber)

  @throws[ToxGroupInviteAcceptException]
  override def groupInviteAccept(inviteData: Array[Byte], password: Array[Byte]): Int =
    ToxCoreJni.toxGroupInviteAccept(instanceNumber, inviteData, password)

  @throws[ToxGroupFounderSetPasswordException]
  override def setGroupFounderPassword(groupNumber: Int, password: Array[Byte]): Unit =
    ToxCoreJni.toxGroupFounderSetPassword(instanceNumber, groupNumber, password)

  @throws[ToxGroupFounderSetPrivacyStateException]
  override def setGroupFounderPrivacyState(groupNumber: Int, privacyState: ToxGroupPrivacyState): Unit =
    ToxCoreJni.toxGroupFounderSetPrivacyState(instanceNumber, groupNumber, privacyState.ordinal)

  @throws[ToxGroupFounderSetPeerLimitException]
  override def setGroupFounderPeerLimit(groupNumber: Int, maxPeers: Int): Unit =
    ToxCoreJni.toxGroupFounderSetPeerLimit(instanceNumber, groupNumber, maxPeers)

  @throws[ToxGroupToggleIgnoreException]
  override def groupToggleIgnore(groupNumber: Int, peerNumber: Int, ignore: Boolean): Unit =
    ToxCoreJni.toxGroupToggleIgnore(instanceNumber, groupNumber, peerNumber, ignore)

  @throws[ToxGroupModSetRoleException]
  override def setGroupModRole(groupNumber: Int, peerNumber: Int, role: ToxGroupRole): Unit =
    ToxCoreJni.toxGroupModSetRole(instanceNumber, groupNumber, peerNumber, role.ordinal)

  @throws[ToxGroupModRemovePeerException]
  override def groupModRemovePeer(groupNumber: Int, peerNumber: Int, setBan: Boolean): Unit =
    ToxCoreJni.toxGroupModRemovePeer(instanceNumber, groupNumber, peerNumber, setBan)

  @throws[ToxGroupModRemoveBanException]
  override def groupModRemoveBan(groupNumber: Int, banId: Short): Unit =
    ToxCoreJni.toxGroupModRemoveBan(instanceNumber, groupNumber, banId)

  @throws[ToxGroupBanQueryException]
  override def getGroupBanList(groupNumber: Int): Array[Short] =
    ToxCoreJni.toxGroupBanGetList(instanceNumber, groupNumber)

  @throws[ToxGroupBanQueryException]
  override def getGroupBanName(groupNumber: Int, banId: Short): Array[Byte] =
    ToxCoreJni.toxGroupBanGetName(instanceNumber, groupNumber, banId)

  @throws[ToxGroupBanQueryException]
  override def getGroupBanTimeSet(groupNumber: Int, banId: Short): Long =
    ToxCoreJni.toxGroupBanGetTimeSet(instanceNumber, groupNumber, banId)

  @throws[ToxFriendCustomPacketException]
  override def sendLossyPacket(friendNumber: Int, data: Array[Byte]): Unit =
    ToxCoreJni.toxSendLossyPacket(instanceNumber, friendNumber, data)

  @throws[ToxFriendCustomPacketException]
  override def sendLosslessPacket(friendNumber: Int, data: Array[Byte]): Unit =
    ToxCoreJni.toxSendLosslessPacket(instanceNumber, friendNumber, data)

  override def callback(handler: ToxEventListener[ToxCoreState]): Unit = {
    this.eventListener = handler
  }

  def invokeFriendName(friendNumber: Int, @NotNull name: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendName(instanceNumber, friendNumber, name)
  def invokeFriendStatusMessage(friendNumber: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendStatusMessage(instanceNumber, friendNumber, message)
  def invokeFriendStatus(friendNumber: Int, @NotNull status: ToxUserStatus): Unit =
    ToxCoreJni.invokeFriendStatus(instanceNumber, friendNumber, status.ordinal())
  def invokeFriendConnectionStatus(friendNumber: Int, @NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeFriendConnectionStatus(instanceNumber, friendNumber, connectionStatus.ordinal())
  def invokeFriendTyping(friendNumber: Int, isTyping: Boolean): Unit =
    ToxCoreJni.invokeFriendTyping(instanceNumber, friendNumber, isTyping)
  def invokeFriendReadReceipt(friendNumber: Int, messageId: Int): Unit =
    ToxCoreJni.invokeFriendReadReceipt(instanceNumber, friendNumber, messageId)
  def invokeFriendRequest(@NotNull publicKey: Array[Byte], timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendRequest(instanceNumber, publicKey, timeDelta, message)
  def invokeFriendMessage(friendNumber: Int, @NotNull `type`: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendMessage(instanceNumber, friendNumber, `type`.ordinal(), timeDelta, message)
  def invokeFileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int): Unit =
    ToxCoreJni.invokeFileChunkRequest(instanceNumber, friendNumber, fileNumber, position, length)
  def invokeFileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecv(instanceNumber, friendNumber, fileNumber, kind, fileSize, filename)
  def invokeFileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecvChunk(instanceNumber, friendNumber, fileNumber, position, data)
  def invokeFileRecvControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl): Unit =
    ToxCoreJni.invokeFileRecvControl(instanceNumber, friendNumber, fileNumber, control.ordinal())
  def invokeFriendLossyPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLossyPacket(instanceNumber, friendNumber, data)
  def invokeFriendLosslessPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLosslessPacket(instanceNumber, friendNumber, data)
  def invokeSelfConnectionStatus(@NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeSelfConnectionStatus(instanceNumber, connectionStatus.ordinal())

}
