#include "ToxCore.h"

using namespace core;

 /*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSendMessage
 * Signature: (III[B)V
 */
TOX_METHOD (void, GroupSendMessage,
  jint instanceNumber, jint groupnumber, jint type, jbyteArray message)
{
    ByteArray message_array(env, message);

    TOX_MESSAGE_TYPE const message_type = [=] {
            switch (type)
              {
              case 0: return TOX_MESSAGE_TYPE_NORMAL;
              case 1: return TOX_MESSAGE_TYPE_ACTION;
              }
            tox4j_fatal ("Invalid message type from Java");
        } ();

    instances.with_instance_ign (env, instanceNumber,
        tox_group_send_message, groupnumber, message_type, message_array.data(), message_array.size()
    );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupNew
 * Signature: (II[B)V
 */
TOX_METHOD (jint, GroupNew,
  jint instanceNumber, jint type, jbyteArray group_name)
{
    ByteArray group_name_array(env, group_name);
    TOX_GROUP_PRIVACY_STATE const privacy_state = [=] {
            switch (type)
              {
              case 0: return TOX_GROUP_PRIVACY_STATE_PUBLIC;
              case 1: return TOX_GROUP_PRIVACY_STATE_PRIVATE;
              }
            tox4j_fatal ("Invalid privacy state from Java");
        } ();

    return instances.with_instance_err (env, instanceNumber,
        identity,
        tox_group_new, privacy_state, group_name_array.data(), group_name_array.size()
    );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupJoin
 * Signature: (I[B[B)V
 */
TOX_METHOD (jint, GroupJoin,
  jint instanceNumber, jbyteArray chatId, jbyteArray password)
{
    ByteArray password_array(env, password);
    ByteArray chat_id_array(env, chatId);
    
    tox4j_assert (!chatId || chat_id_array.size () == TOX_GROUP_CHAT_ID_SIZE);
  
    return instances.with_instance_err (env, instanceNumber,
        identity,
        tox_group_join, chat_id_array.data(),  password_array.data(), password_array.size()
    );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupReconnect
 * Signature: (II)V
 */
TOX_METHOD (void, GroupReconnect,
  jint instanceNumber, jint groupnumber)
{
  instances.with_instance_ign (env, instanceNumber,
    tox_group_reconnect, groupnumber
  );
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupLeave
 * Signature: (II[B)V
 */
TOX_METHOD (void, GroupLeave,
  jint instanceNumber, jint groupnumber, jbyteArray message)
{
    ByteArray message_array(env, message);
    instances.with_instance_ign (env, instanceNumber,
        tox_group_leave, groupnumber, message_array.data(), message_array.size()
    );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSelfSetName
 * Signature: (II[B)V
 */
TOX_METHOD (void, GroupSelfSetName,
  jint instanceNumber, jint groupnumber, jbyteArray name)
{
    ByteArray name_array(env, name);

    return instances.with_instance_ign (env, instanceNumber,
        tox_group_self_set_name, groupnumber, name_array.data(), name_array.size()
    );
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSelfGetName
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, GroupSelfGetName,
  jint instanceNumber, jint groupnumber)
{
    return instances.with_instance_err (env, instanceNumber,
        [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
        get_vector_err<uint8_t, TOX_ERR_GROUP_SELF_QUERY, TOX_ERR_GROUP_SELF_QUERY_OK, uint32_t>
        ::compose<tox_group_self_get_name_size, tox_group_self_get_name>, groupnumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSelfSetStatus
 * Signature: (II)V
 */
TOX_METHOD (void, GroupSelfSetStatus,
  jint instanceNumber, jint groupnumber, jint status)
{
  TOX_USER_STATUS const status_enum = [=] {
    switch (status)
      {
      case 0: return TOX_USER_STATUS_NONE;
      case 1: return TOX_USER_STATUS_AWAY;
      case 2: return TOX_USER_STATUS_BUSY;
      }
    tox4j_fatal ("Invalid group  status from Java");
  } ();
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_self_set_status, groupnumber, status_enum
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSelfGetStatus
 * Signature: (II)I
 */
TOX_METHOD (jint, GroupSelfGetStatus,
  jint instanceNumber, jint groupnumber)
{
    switch (instances.with_instance_err (env, instanceNumber, identity, tox_group_self_get_status, groupnumber))
      {
    case TOX_USER_STATUS_NONE: return 0;
    case TOX_USER_STATUS_AWAY: return 1;
    case TOX_USER_STATUS_BUSY: return 2;
      }
    tox4j_fatal ("Invalid result from tox_group_self_get_status");
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSelfGetRole
 * Signature: (II)I
 */
TOX_METHOD (jint, GroupSelfGetRole,
  jint instanceNumber, jint groupnumber)
{

    switch (instances.with_instance_err (env, instanceNumber, identity, tox_group_self_get_role, groupnumber))
      {
      case TOX_GROUP_ROLE_FOUNDER: return 0;
      case TOX_GROUP_ROLE_MODERATOR: return 1;
      case TOX_GROUP_ROLE_USER: return 2;
      case TOX_GROUP_ROLE_OBSERVER: return 3;
      }
    tox4j_fatal ("Invalid result from tox_group_self_get_role");
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupPeerGetName
 * Signature: (III)[B
 */
TOX_METHOD (jbyteArray, GroupPeerGetName,
  jint instanceNumber, jint groupnumber, jint peernumber)
{
  return instances.with_instance_err (env, instanceNumber,
          [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
          get_vector_err<uint8_t, TOX_ERR_GROUP_PEER_QUERY, TOX_ERR_GROUP_PEER_QUERY_OK, uint32_t, uint32_t>
          ::compose<tox_group_peer_get_name_size, tox_group_peer_get_name>, groupnumber, peernumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupPeerGetStatus
 * Signature: (III)I
 */
TOX_METHOD (jint, GroupPeerGetStatus,
  jint instanceNumber, jint groupnumber, jint peernumber)
{
      switch (instances.with_instance_err (env, instanceNumber, identity, tox_group_peer_get_status, groupnumber, peernumber))
        {
    case TOX_USER_STATUS_NONE: return 0;
    case TOX_USER_STATUS_AWAY: return 1;
    case TOX_USER_STATUS_BUSY: return 2;
        }
      tox4j_fatal ("Invalid result from tox_group_peer_get_status");
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupPeerGetRole
 * Signature: (III)I
 */
TOX_METHOD (jint, GroupPeerGetRole,
  jint instanceNumber, jint groupnumber, jint peernumber)
{

        switch (instances.with_instance_err (env, instanceNumber, identity, tox_group_peer_get_role, groupnumber, peernumber))
          {
      case TOX_GROUP_ROLE_FOUNDER: return 0;
      case TOX_GROUP_ROLE_MODERATOR: return 1;
      case TOX_GROUP_ROLE_USER: return 2;
      case TOX_GROUP_ROLE_OBSERVER: return 3;
          }
        tox4j_fatal ("Invalid result from tox_group_peer_get_role");
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSetTopic
 * Signature: (II[B)V
 */
TOX_METHOD (void, GroupSetTopic,
  jint instanceNumber, jint groupnumber, jbyteArray topic)
{
  ByteArray topic_array(env, topic);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_topic, groupnumber, topic_array.data(), topic_array.size()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetTopic
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, GroupGetTopic,
  jint instanceNumber, jint groupnumber)
{
  return instances.with_instance_err (env, instanceNumber,
          [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
          get_vector_err<uint8_t, TOX_ERR_GROUP_STATE_QUERIES, TOX_ERR_GROUP_STATE_QUERIES_OK, uint32_t>
          ::compose<tox_group_get_topic_size, tox_group_get_topic>, groupnumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetName
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, GroupGetName,
  jint instanceNumber, jint groupnumber)
{
    return instances.with_instance_err (env, instanceNumber,
            [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
            get_vector_err<uint8_t, TOX_ERR_GROUP_STATE_QUERIES, TOX_ERR_GROUP_STATE_QUERIES_OK, uint32_t>
            ::compose<tox_group_get_name_size, tox_group_get_name>, groupnumber);
}

//FIXME
size_t
tox_group_get_chat_id_size (const Tox *tox, uint32_t groupnumber, TOX_ERR_GROUP_STATE_QUERIES *error)
{
 return TOX_GROUP_CHAT_ID_SIZE;
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetChatId
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, GroupGetChatId,
  jint instanceNumber, jint groupnumber)
{
    return instances.with_instance_err (env, instanceNumber,
        [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
        get_vector_err<uint8_t, TOX_ERR_GROUP_STATE_QUERIES, TOX_ERR_GROUP_STATE_QUERIES_OK, uint32_t>
        ::compose<tox_group_get_chat_id_size, tox_group_get_chat_id>, groupnumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetNumberPeers
 * Signature: (II)I
 */
TOX_METHOD (jint, GroupGetNumberPeers,
  jint instanceNumber, jint groupnumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_number_peers, groupnumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetNumberGroups
 * Signature: (I)I
 */
TOX_METHOD (jint, GroupGetNumberGroups,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_group_get_number_groups
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetPrivacyState
 * Signature: (II)I
 */
TOX_METHOD (jint, GroupGetPrivacyState,
  jint instanceNumber, jint groupnumber)
{
      switch (instances.with_instance_err (env, instanceNumber, identity, tox_group_get_privacy_state, groupnumber))
        {
      case TOX_GROUP_PRIVACY_STATE_PUBLIC: return 0;
      case TOX_GROUP_PRIVACY_STATE_PRIVATE: return 1;
        }
      tox4j_fatal ("Invalid result from tox_group_get_privacy_state");
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetPeerLimit
 * Signature: (II)I
 */
TOX_METHOD (jint, GroupGetPeerLimit,
  jint instanceNumber, jint groupnumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_peer_limit, groupnumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupGetPassword
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, GroupGetPassword,
  jint instanceNumber, jint groupnumber)
{
      return instances.with_instance_err (env, instanceNumber,
              [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
              get_vector_err<uint8_t, TOX_ERR_GROUP_STATE_QUERIES, TOX_ERR_GROUP_STATE_QUERIES_OK, uint32_t>
              ::compose<tox_group_get_password_size, tox_group_get_password>, groupnumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupSendPrivateMessage
 * Signature: (III[B)V
 */
TOX_METHOD (void, GroupSendPrivateMessage,
  jint instanceNumber, jint groupnumber, jint peernumber, jbyteArray message)
{
    ByteArray message_array(env, message);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_send_private_message, groupnumber, peernumber, message_array.data(), message_array.size()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupInviteFriend
 * Signature: (III)V
 */
TOX_METHOD (void, GroupInviteFriend,
  jint instanceNumber, jint groupnumber, jint friendnumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_invite_friend, groupnumber, friendnumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupInviteAccept
 * Signature: (I[B[B)V
 */
TOX_METHOD (jint, GroupInviteAccept,
  jint instanceNumber, jbyteArray invite_data, jbyteArray password)
{
    ByteArray invite_data_array(env, invite_data);
    ByteArray password_array(env, password);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_invite_accept, invite_data_array.data(), invite_data_array.size(), password_array.data(), password_array.size()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupFounderSetPassword
 * Signature: (II[B)V
 */
TOX_METHOD (void, GroupFounderSetPassword,
  jint instanceNumber, jint groupnumber, jbyteArray password)
{
    ByteArray password_array(env, password);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_founder_set_password, groupnumber, password_array.data(), password_array.size()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupFounderSetPrivacyState
 * Signature: (III)V
 */
TOX_METHOD (void, GroupFounderSetPrivacyState,
  jint instanceNumber, jint groupnumber, jint type)
{
    TOX_GROUP_PRIVACY_STATE const privacy_state = [=] {
            switch (type)
              {
              case 0: return TOX_GROUP_PRIVACY_STATE_PUBLIC;
              case 1: return TOX_GROUP_PRIVACY_STATE_PRIVATE;
              }
            tox4j_fatal ("Invalid privacy state from Java");
        } ();
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_founder_set_privacy_state, groupnumber, privacy_state
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupFounderSetPeerLimit
 * Signature: (III)V
 */
TOX_METHOD (void, GroupFounderSetPeerLimit,
  jint instanceNumber, jint groupnumber, jint max_peers)
{

  return instances.with_instance_ign (env, instanceNumber,
    tox_group_founder_set_peer_limit, groupnumber, max_peers
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupToggleIgnore
 * Signature: (IIIZ)V
 */
TOX_METHOD (void, GroupToggleIgnore,
  jint instanceNumber, jint groupnumber, jint peernumber, jboolean ignore)
{

  return instances.with_instance_ign (env, instanceNumber,
    tox_group_toggle_ignore, groupnumber, peernumber, ignore
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupModSetRole
 * Signature: (IIII)V
 */
TOX_METHOD (void, GroupModSetRole,
  jint instanceNumber, jint groupnumber, jint peernumber, jint type)
{
    TOX_GROUP_ROLE const role = [=] {
            switch (type)
              {
              case 0: return TOX_GROUP_ROLE_FOUNDER;
              case 1: return TOX_GROUP_ROLE_MODERATOR;
              case 2: return TOX_GROUP_ROLE_USER;
              case 3: return TOX_GROUP_ROLE_OBSERVER;
              }
            tox4j_fatal ("Invalid group role from Java");
        } ();
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_mod_set_role, groupnumber, peernumber, role
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupModRemovePeer
 * Signature: (IIIZ)V
 */
TOX_METHOD (void, GroupModRemovePeer,
  jint instanceNumber, jint groupnumber, jint peernumber, jboolean set_ban)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_mod_remove_peer, groupnumber, peernumber, set_ban
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupModRemoveBan
 * Signature: (III)V
 */
TOX_METHOD (void, GroupModRemoveBan,
  jint instanceNumber, jint groupnumber, jshort ban_id)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_mod_remove_ban, groupnumber, ban_id
  );
}

//FIXME
bool
new_tox_group_ban_get_list(const Tox *tox, uint32_t groupnumber, int16_t *list, TOX_ERR_GROUP_BAN_QUERY *error)
{
    return tox_group_ban_get_list(tox, groupnumber, (uint16_t*) list, error);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupBanGetList
 * Signature: (II)[I
 */
TOX_METHOD (jshortArray, GroupBanGetList,
  jint instanceNumber, jint groupnumber)
{
        return instances.with_instance_err (env, instanceNumber,
                [=](std::vector<int16_t> v) { return toJavaArray (env, v); },
                get_vector_err<int16_t, TOX_ERR_GROUP_BAN_QUERY, TOX_ERR_GROUP_BAN_QUERY_OK, uint32_t>
                ::compose<tox_group_ban_get_list_size, new_tox_group_ban_get_list>, groupnumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupBanGetName
 * Signature: (III)[B
 */
TOX_METHOD (jbyteArray, GroupBanGetName,
  jint instanceNumber, jint groupnumber, jshort ban_id)
{
          return instances.with_instance_err (env, instanceNumber,
                  [=](std::vector<uint8_t> v) { return toJavaArray (env, v); },
                  get_vector_err<uint8_t, TOX_ERR_GROUP_BAN_QUERY, TOX_ERR_GROUP_BAN_QUERY_OK, uint32_t, uint16_t>
                  ::compose<tox_group_ban_get_name_size, tox_group_ban_get_name>, groupnumber, ban_id);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGroupBanGetTimeSet
 * Signature: (III)I
 */
TOX_METHOD (jlong, GroupBanGetTimeSet,
  jint instanceNumber, jint groupnumber, jshort ban_id)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_ban_get_time_set, groupnumber, ban_id
  );
}
