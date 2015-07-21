#include "ToxCore.h"

using namespace core;


ToxInstances<tox::core_ptr, std::unique_ptr<Events>> core::instances;

template<> extern char const *const module_name<Tox> = "core";
template<> extern char const *const exn_prefix<Tox> = "";


HANDLE ("SetInfo", SET_INFO)
{
  switch (error)
    {
    success_case (SET_INFO);
    failure_case (SET_INFO, NULL);
    failure_case (SET_INFO, TOO_LONG);
    }
  return unhandled ();
}


HANDLE ("Bootstrap", BOOTSTRAP)
{
  switch (error)
    {
    success_case (BOOTSTRAP);
    failure_case (BOOTSTRAP, NULL);
    failure_case (BOOTSTRAP, BAD_HOST);
    failure_case (BOOTSTRAP, BAD_PORT);
    }
  return unhandled ();
}


HANDLE ("FileControl", FILE_CONTROL)
{
  switch (error)
    {
    success_case (FILE_CONTROL);
    failure_case (FILE_CONTROL, FRIEND_NOT_FOUND);
    failure_case (FILE_CONTROL, FRIEND_NOT_CONNECTED);
    failure_case (FILE_CONTROL, NOT_FOUND);
    failure_case (FILE_CONTROL, NOT_PAUSED);
    failure_case (FILE_CONTROL, DENIED);
    failure_case (FILE_CONTROL, ALREADY_PAUSED);
    failure_case (FILE_CONTROL, SENDQ);
    }
  return unhandled ();
}


HANDLE ("FileSeek", FILE_SEEK)
{
  switch (error)
    {
    success_case (FILE_SEEK);
    failure_case (FILE_SEEK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEEK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEEK, NOT_FOUND);
    failure_case (FILE_SEEK, DENIED);
    failure_case (FILE_SEEK, INVALID_POSITION);
    failure_case (FILE_SEEK, SENDQ);
    }
  return unhandled ();
}


HANDLE ("FileSend", FILE_SEND)
{
  switch (error)
    {
    success_case (FILE_SEND);
    failure_case (FILE_SEND, NULL);
    failure_case (FILE_SEND, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND, NAME_TOO_LONG);
    failure_case (FILE_SEND, TOO_MANY);
    }
  return unhandled ();
}


HANDLE ("FileSendChunk", FILE_SEND_CHUNK)
{
  switch (error)
    {
    success_case (FILE_SEND_CHUNK);
    failure_case (FILE_SEND_CHUNK, NULL);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND_CHUNK, NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, NOT_TRANSFERRING);
    failure_case (FILE_SEND_CHUNK, INVALID_LENGTH);
    failure_case (FILE_SEND_CHUNK, SENDQ);
    failure_case (FILE_SEND_CHUNK, WRONG_POSITION);
    }
  return unhandled ();
}


HANDLE ("GetPort", GET_PORT)
{
  switch (error)
    {
    success_case (GET_PORT);
    failure_case (GET_PORT, NOT_BOUND);
    }
  return unhandled ();
}


HANDLE ("FriendCustomPacket", FRIEND_CUSTOM_PACKET)
{
  switch (error)
    {
    success_case (FRIEND_CUSTOM_PACKET);
    failure_case (FRIEND_CUSTOM_PACKET, NULL);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_FOUND);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_CUSTOM_PACKET, INVALID);
    failure_case (FRIEND_CUSTOM_PACKET, EMPTY);
    failure_case (FRIEND_CUSTOM_PACKET, TOO_LONG);
    failure_case (FRIEND_CUSTOM_PACKET, SENDQ);
    }
  return unhandled ();
}


HANDLE ("FriendAdd", FRIEND_ADD)
{
  switch (error)
    {
    success_case (FRIEND_ADD);
    failure_case (FRIEND_ADD, NULL);
    failure_case (FRIEND_ADD, TOO_LONG);
    failure_case (FRIEND_ADD, NO_MESSAGE);
    failure_case (FRIEND_ADD, OWN_KEY);
    failure_case (FRIEND_ADD, ALREADY_SENT);
    failure_case (FRIEND_ADD, BAD_CHECKSUM);
    failure_case (FRIEND_ADD, SET_NEW_NOSPAM);
    failure_case (FRIEND_ADD, MALLOC);
    }

  return unhandled ();
}


HANDLE ("FriendDelete", FRIEND_DELETE)
{
  switch (error)
    {
    success_case (FRIEND_DELETE);
    failure_case (FRIEND_DELETE, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}


HANDLE ("FriendByPublicKey", FRIEND_BY_PUBLIC_KEY)
{
  switch (error)
    {
    success_case (FRIEND_BY_PUBLIC_KEY);
    failure_case (FRIEND_BY_PUBLIC_KEY, NULL);
    failure_case (FRIEND_BY_PUBLIC_KEY, NOT_FOUND);
    }
  return unhandled ();
}


HANDLE ("FriendGetPublicKey", FRIEND_GET_PUBLIC_KEY)
{
  switch (error)
    {
    success_case (FRIEND_GET_PUBLIC_KEY);
    failure_case (FRIEND_GET_PUBLIC_KEY, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}


HANDLE ("SetTyping", SET_TYPING)
{
  switch (error)
    {
    success_case (SET_TYPING);
    failure_case (SET_TYPING, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}


HANDLE ("FriendSendMessage", FRIEND_SEND_MESSAGE)
{
  switch (error)
    {
    success_case (FRIEND_SEND_MESSAGE);
    failure_case (FRIEND_SEND_MESSAGE, NULL);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_FOUND);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_SEND_MESSAGE, SENDQ);
    failure_case (FRIEND_SEND_MESSAGE, TOO_LONG);
    failure_case (FRIEND_SEND_MESSAGE, EMPTY);
    }

  return unhandled ();
}

HANDLE ("New", NEW)
{
  switch (error)
    {
    success_case (NEW);
    failure_case (NEW, NULL);
    failure_case (NEW, MALLOC);
    failure_case (NEW, PORT_ALLOC);
    failure_case (NEW, PROXY_BAD_TYPE);
    failure_case (NEW, PROXY_BAD_HOST);
    failure_case (NEW, PROXY_BAD_PORT);
    failure_case (NEW, PROXY_NOT_FOUND);
    failure_case (NEW, LOAD_ENCRYPTED);
    failure_case (NEW, LOAD_BAD_FORMAT);
    }
  return unhandled ();
}

HANDLE ("FileGet", FILE_GET)
{
  switch (error)
    {
    success_case (FILE_GET);
    failure_case (FILE_GET, NULL);
    failure_case (FILE_GET, FRIEND_NOT_FOUND);
    failure_case (FILE_GET, NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupStateQueries", GROUP_STATE_QUERIES)
{
  switch (error)
    {
    success_case (GROUP_STATE_QUERIES);
		failure_case (GROUP_STATE_QUERIES, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupInviteFriend", GROUP_INVITE_FRIEND)
{
  switch (error)
    {
    success_case (GROUP_INVITE_FRIEND);
		failure_case (GROUP_INVITE_FRIEND, GROUP_NOT_FOUND);
		failure_case (GROUP_INVITE_FRIEND, FRIEND_NOT_FOUND);
		failure_case (GROUP_INVITE_FRIEND, INVITE_FAIL);
		failure_case (GROUP_INVITE_FRIEND, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupFounderSetPeerLimit", GROUP_FOUNDER_SET_PEER_LIMIT)
{
  switch (error)
    {
    success_case (GROUP_FOUNDER_SET_PEER_LIMIT);
		failure_case (GROUP_FOUNDER_SET_PEER_LIMIT, GROUP_NOT_FOUND);
		failure_case (GROUP_FOUNDER_SET_PEER_LIMIT, PERMISSIONS);
		failure_case (GROUP_FOUNDER_SET_PEER_LIMIT, FAIL_SET);
		failure_case (GROUP_FOUNDER_SET_PEER_LIMIT, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupToggleIgnore", GROUP_TOGGLE_IGNORE)
{
  switch (error)
    {
    success_case (GROUP_TOGGLE_IGNORE);
		failure_case (GROUP_TOGGLE_IGNORE, GROUP_NOT_FOUND);
		failure_case (GROUP_TOGGLE_IGNORE, PEER_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupModRemovePeer", GROUP_MOD_REMOVE_PEER)
{
  switch (error)
    {
    success_case (GROUP_MOD_REMOVE_PEER);
		failure_case (GROUP_MOD_REMOVE_PEER, GROUP_NOT_FOUND);
		failure_case (GROUP_MOD_REMOVE_PEER, PEER_NOT_FOUND);
		failure_case (GROUP_MOD_REMOVE_PEER, PERMISSIONS);
		failure_case (GROUP_MOD_REMOVE_PEER, FAIL_ACTION);
		failure_case (GROUP_MOD_REMOVE_PEER, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupReconnect", GROUP_RECONNECT)
{
  switch (error)
    {
    success_case (GROUP_RECONNECT);
		failure_case (GROUP_RECONNECT, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupSelfStatusSet", GROUP_SELF_STATUS_SET)
{
  switch (error)
    {
    success_case (GROUP_SELF_STATUS_SET);
		failure_case (GROUP_SELF_STATUS_SET, GROUP_NOT_FOUND);
		failure_case (GROUP_SELF_STATUS_SET, INVALID);
		failure_case (GROUP_SELF_STATUS_SET, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupFounderSetPassword", GROUP_FOUNDER_SET_PASSWORD)
{
  switch (error)
    {
    success_case (GROUP_FOUNDER_SET_PASSWORD);
		failure_case (GROUP_FOUNDER_SET_PASSWORD, GROUP_NOT_FOUND);
		failure_case (GROUP_FOUNDER_SET_PASSWORD, PERMISSIONS);
		failure_case (GROUP_FOUNDER_SET_PASSWORD, TOO_LONG);
		failure_case (GROUP_FOUNDER_SET_PASSWORD, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupLeave", GROUP_LEAVE)
{
  switch (error)
    {
    success_case (GROUP_LEAVE);
		failure_case (GROUP_LEAVE, GROUP_NOT_FOUND);
		failure_case (GROUP_LEAVE, TOO_LONG);
		failure_case (GROUP_LEAVE, FAIL_SEND);
		failure_case (GROUP_LEAVE, DELETE_FAIL);
    }
  return unhandled ();
}

HANDLE ("GroupSendPrivateMessage", GROUP_SEND_PRIVATE_MESSAGE)
{
  switch (error)
    {
    success_case (GROUP_SEND_PRIVATE_MESSAGE);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, GROUP_NOT_FOUND);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, PEER_NOT_FOUND);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, TOO_LONG);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, EMPTY);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, PERMISSIONS);
		failure_case (GROUP_SEND_PRIVATE_MESSAGE, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupInviteAccept", GROUP_INVITE_ACCEPT)
{
  switch (error)
    {
    success_case (GROUP_INVITE_ACCEPT);
		failure_case (GROUP_INVITE_ACCEPT, BAD_INVITE);
		failure_case (GROUP_INVITE_ACCEPT, INIT_FAILED);
		failure_case (GROUP_INVITE_ACCEPT, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupModSetRole", GROUP_MOD_SET_ROLE)
{
  switch (error)
    {
    success_case (GROUP_MOD_SET_ROLE);
		failure_case (GROUP_MOD_SET_ROLE, GROUP_NOT_FOUND);
		failure_case (GROUP_MOD_SET_ROLE, PEER_NOT_FOUND);
		failure_case (GROUP_MOD_SET_ROLE, PERMISSIONS);
		failure_case (GROUP_MOD_SET_ROLE, ASSIGNMENT);
		failure_case (GROUP_MOD_SET_ROLE, FAIL_ACTION);
    }
  return unhandled ();
}

HANDLE ("GroupPeerQuery", GROUP_PEER_QUERY)
{
  switch (error)
    {
    success_case (GROUP_PEER_QUERY);
		failure_case (GROUP_PEER_QUERY, GROUP_NOT_FOUND);
		failure_case (GROUP_PEER_QUERY, PEER_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupTopicSet", GROUP_TOPIC_SET)
{
  switch (error)
    {
    success_case (GROUP_TOPIC_SET);
		failure_case (GROUP_TOPIC_SET, GROUP_NOT_FOUND);
		failure_case (GROUP_TOPIC_SET, TOO_LONG);
		failure_case (GROUP_TOPIC_SET, PERMISSIONS);
		failure_case (GROUP_TOPIC_SET, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupNew", GROUP_NEW)
{
  switch (error)
    {
    success_case (GROUP_NEW);
		failure_case (GROUP_NEW, TOO_LONG);
		failure_case (GROUP_NEW, EMPTY);
		failure_case (GROUP_NEW, PRIVACY);
		failure_case (GROUP_NEW, INIT);
		failure_case (GROUP_NEW, STATE);
		failure_case (GROUP_NEW, ANNOUNCE);
    }
  return unhandled ();
}

HANDLE ("GroupSelfQuery", GROUP_SELF_QUERY)
{
  switch (error)
    {
    success_case (GROUP_SELF_QUERY);
		failure_case (GROUP_SELF_QUERY, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupJoin", GROUP_JOIN)
{
  switch (error)
    {
    success_case (GROUP_JOIN);
		failure_case (GROUP_JOIN, INIT);
		failure_case (GROUP_JOIN, BAD_CHAT_ID);
		failure_case (GROUP_JOIN, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSelfNameSet", GROUP_SELF_NAME_SET)
{
  switch (error)
    {
    success_case (GROUP_SELF_NAME_SET);
		failure_case (GROUP_SELF_NAME_SET, GROUP_NOT_FOUND);
		failure_case (GROUP_SELF_NAME_SET, TOO_LONG);
		failure_case (GROUP_SELF_NAME_SET, INVALID);
		failure_case (GROUP_SELF_NAME_SET, TAKEN);
		failure_case (GROUP_SELF_NAME_SET, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupSendMessage", GROUP_SEND_MESSAGE)
{
  switch (error)
    {
    success_case (GROUP_SEND_MESSAGE);
		failure_case (GROUP_SEND_MESSAGE, GROUP_NOT_FOUND);
		failure_case (GROUP_SEND_MESSAGE, TOO_LONG);
		failure_case (GROUP_SEND_MESSAGE, EMPTY);
		failure_case (GROUP_SEND_MESSAGE, BAD_TYPE);
		failure_case (GROUP_SEND_MESSAGE, PERMISSIONS);
		failure_case (GROUP_SEND_MESSAGE, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupFounderSetPrivacyState", GROUP_FOUNDER_SET_PRIVACY_STATE)
{
  switch (error)
    {
    success_case (GROUP_FOUNDER_SET_PRIVACY_STATE);
		failure_case (GROUP_FOUNDER_SET_PRIVACY_STATE, GROUP_NOT_FOUND);
		failure_case (GROUP_FOUNDER_SET_PRIVACY_STATE, INVALID);
		failure_case (GROUP_FOUNDER_SET_PRIVACY_STATE, PERMISSIONS);
		failure_case (GROUP_FOUNDER_SET_PRIVACY_STATE, FAIL_SET);
		failure_case (GROUP_FOUNDER_SET_PRIVACY_STATE, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupModRemoveBan", GROUP_MOD_REMOVE_BAN)
{
  switch (error)
    {
    success_case (GROUP_MOD_REMOVE_BAN);
		failure_case (GROUP_MOD_REMOVE_BAN, GROUP_NOT_FOUND);
		failure_case (GROUP_MOD_REMOVE_BAN, PERMISSIONS);
		failure_case (GROUP_MOD_REMOVE_BAN, FAIL_ACTION);
		failure_case (GROUP_MOD_REMOVE_BAN, FAIL_SEND);
    }
  return unhandled ();
}

HANDLE ("GroupBanQuery", GROUP_BAN_QUERY)
{
  switch (error)
    {
    success_case (GROUP_BAN_QUERY);
		failure_case (GROUP_BAN_QUERY, GROUP_NOT_FOUND);
		failure_case (GROUP_BAN_QUERY, BAD_ID);
    }
  return unhandled ();
}