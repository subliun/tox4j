#include "ToxCore.h"

using namespace core;


template<typename Message>
static void
set_connection_status (Message &msg, TOX_CONNECTION connection_status)
{
  using proto::Connection;
  switch (connection_status)
    {
    case TOX_CONNECTION_NONE:
      msg->set_connection_status (Connection::NONE);
      break;
    case TOX_CONNECTION_TCP:
      msg->set_connection_status (Connection::TCP);
      break;
    case TOX_CONNECTION_UDP:
      msg->set_connection_status (Connection::UDP);
      break;
    }
}

template<typename Message>
static void
set_message_type (Message &msg, TOX_MESSAGE_TYPE type)
{
  using proto::MessageType;
  switch (type)
    {
    case TOX_MESSAGE_TYPE_NORMAL:
      msg->set_type (MessageType::NORMAL);
      break;
    case TOX_MESSAGE_TYPE_ACTION:
      msg->set_type (MessageType::ACTION);
      break;
    }
}

template<typename Message>
static void
set_user_status (Message &msg, TOX_USER_STATUS status)
{
    using proto::UserStatus;
    switch (status)
      {
      case TOX_USER_STATUS_NONE:
        msg->set_status (UserStatus::NONE);
        break;
      case TOX_USER_STATUS_AWAY:
        msg->set_status (UserStatus::AWAY);
        break;
      case TOX_USER_STATUS_BUSY:
        msg->set_status (UserStatus::BUSY);
        break;
      }
}

static void
tox4j_self_connection_status_cb (Tox *tox, TOX_CONNECTION connection_status, Events &events)
{
  debug_log (tox4j_self_connection_status_cb, tox, connection_status);
  auto msg = events.add_self_connection_status ();
  set_connection_status (msg, connection_status);
}

static void
tox4j_friend_name_cb (Tox *tox, uint32_t friend_number, uint8_t const *name, size_t length, Events &events)
{
  debug_log (tox4j_friend_name_cb, tox, friend_number, name, length);
  auto msg = events.add_friend_name ();
  msg->set_friend_number (friend_number);
  msg->set_name (name, length);
}

static void
tox4j_friend_status_message_cb (Tox *tox, uint32_t friend_number, uint8_t const *message, size_t length, Events &events)
{
  debug_log (tox4j_friend_status_message_cb, tox, friend_number, message, length);
  auto msg = events.add_friend_status_message ();
  msg->set_friend_number (friend_number);
  msg->set_message (message, length);
}

static void
tox4j_friend_status_cb (Tox *tox, uint32_t friend_number, TOX_USER_STATUS status, Events &events)
{
  debug_log (tox4j_friend_status_cb, tox, friend_number, status);
  auto msg = events.add_friend_status ();
  msg->set_friend_number (friend_number);

  set_user_status(msg, status);
}

static void
tox4j_friend_connection_status_cb (Tox *tox, uint32_t friend_number, TOX_CONNECTION connection_status, Events &events)
{
  debug_log (tox4j_friend_connection_status_cb, tox, friend_number, connection_status);
  auto msg = events.add_friend_connection_status ();
  msg->set_friend_number (friend_number);
  set_connection_status (msg, connection_status);
}

static void
tox4j_friend_typing_cb (Tox *tox, uint32_t friend_number, bool is_typing, Events &events)
{
  debug_log (tox4j_friend_typing_cb, tox, friend_number, is_typing);
  auto msg = events.add_friend_typing ();
  msg->set_friend_number (friend_number);
  msg->set_is_typing (is_typing);
}

static void
tox4j_friend_read_receipt_cb (Tox *tox, uint32_t friend_number, uint32_t message_id, Events &events)
{
  debug_log (tox4j_friend_read_receipt_cb, tox, friend_number, message_id);
  auto msg = events.add_friend_read_receipt ();
  msg->set_friend_number (friend_number);
  msg->set_message_id (message_id);
}

static void
tox4j_friend_request_cb (Tox *tox, uint8_t const *public_key, /*uint32_t time_delta, */ uint8_t const *message, size_t length, Events &events)
{
  debug_log (tox4j_friend_request_cb, tox, public_key, message, length);
  auto msg = events.add_friend_request ();
  msg->set_public_key (public_key, TOX_PUBLIC_KEY_SIZE);
  msg->set_time_delta (0);
  msg->set_message (message, length);
}

static void
tox4j_friend_message_cb (Tox *tox, uint32_t friend_number, TOX_MESSAGE_TYPE type, /*uint32_t time_delta, */ uint8_t const *message, size_t length, Events &events)
{
  debug_log (tox4j_friend_message_cb, tox, friend_number, message, length);
  auto msg = events.add_friend_message ();
  msg->set_friend_number (friend_number);
  set_message_type (msg, type);
  msg->set_time_delta (0);
  msg->set_message (message, length);
}

static void
tox4j_file_recv_control_cb (Tox *tox, uint32_t friend_number, uint32_t file_number, TOX_FILE_CONTROL control, Events &events)
{
  debug_log (tox4j_file_recv_control_cb, tox, friend_number, file_number, control);
  auto msg = events.add_file_recv_control ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);

  using proto::FileControl;
  switch (control)
    {
    case TOX_FILE_CONTROL_RESUME:
      msg->set_control (FileControl::RESUME);
      break;
    case TOX_FILE_CONTROL_PAUSE:
      msg->set_control (FileControl::PAUSE);
      break;
    case TOX_FILE_CONTROL_CANCEL:
      msg->set_control (FileControl::CANCEL);
      break;
    }
}

static void
tox4j_file_chunk_request_cb (Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, size_t length, Events &events)
{
  debug_log (tox4j_file_chunk_request_cb, tox, friend_number, file_number, position, length);
  auto msg = events.add_file_chunk_request ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_length (length);
}

static void
tox4j_file_recv_cb (Tox *tox, uint32_t friend_number, uint32_t file_number, uint32_t kind, uint64_t file_size, uint8_t const *filename, size_t filename_length, Events &events)
{
  debug_log (tox4j_file_recv_cb, tox, friend_number, file_number, kind, file_size, filename, filename_length);
  auto msg = events.add_file_recv ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_kind (kind);
  msg->set_file_size (file_size);
  msg->set_filename (filename, filename_length);
}

static void
tox4j_file_recv_chunk_cb (Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, uint8_t const *data, size_t length, Events &events)
{
  debug_log (tox4j_file_recv_chunk_cb, tox, friend_number, file_number, position, data, length);
  auto msg = events.add_file_recv_chunk ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossy_packet_cb (Tox *tox, uint32_t friend_number, uint8_t const *data, size_t length, Events &events)
{
  debug_log (tox4j_friend_lossy_packet_cb, tox, friend_number, data, length);
  auto msg = events.add_friend_lossy_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossless_packet_cb (Tox *tox, uint32_t friend_number, uint8_t const *data, size_t length, Events &events)
{
  debug_log (tox4j_friend_lossless_packet_cb, tox, friend_number, data, length);
  auto msg = events.add_friend_lossless_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}

static void
tox4j_group_peer_name_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, const uint8_t *name, size_t length, Events &events)
{
    debug_log (tox4j_group_peer_name_cb, tox, group_number, peer_number, name, length);
    auto msg = events.add_group_peer_name ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
	msg->set_name (name, length);
}

static void
tox4j_group_peer_status_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, TOX_USER_STATUS status, Events &events)
{
    debug_log (tox4j_group_peer_status_cb, tox, group_number, peer_number, status);
    auto msg = events.add_group_peer_status ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);

    set_user_status(msg, status);
}

static void
tox4j_group_topic_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, const uint8_t *topic, size_t length, Events &events)
{
    debug_log (tox4j_group_topic_cb, tox, group_number, peer_number, topic, length);
    auto msg = events.add_group_topic ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
	msg->set_topic (topic, length);
}

static void
tox4j_group_privacy_state_cb (Tox *tox, uint32_t group_number, TOX_GROUP_PRIVACY_STATE privacy_state, Events &events)
{
    debug_log (tox4j_group_privacy_state_cb, tox, group_number, privacy_state);
    auto msg = events.add_group_privacy_state ();
	msg->set_group_number (group_number);

	using proto::PrivacyState;
	switch (privacy_state)
	  {
	  case TOX_GROUP_PRIVACY_STATE_PUBLIC:
	    msg->set_privacy_state (PrivacyState::PUBLIC);
	    break;
	  case TOX_GROUP_PRIVACY_STATE_PRIVATE:
	    msg->set_privacy_state (PrivacyState::PRIVATE);
      	break;
	  }
}

static void
tox4j_group_peer_limit_cb (Tox *tox, uint32_t group_number, uint32_t peer_limit, Events &events)
{
    debug_log (tox4j_group_peer_limit_cb, tox, group_number, peer_limit);
    auto msg = events.add_group_peer_limit ();
	msg->set_group_number (group_number);
	msg->set_peer_limit (peer_limit);
}

static void
tox4j_group_password_cb (Tox *tox, uint32_t group_number, const uint8_t *password, size_t length, Events &events)
{
    debug_log (tox4j_group_password_cb, tox, group_number, password, length);
    auto msg = events.add_group_password ();
	msg->set_group_number (group_number);
	msg->set_password (password, length);
}

static void
tox4j_group_peerlist_update_cb (Tox *tox, uint32_t group_number, Events &events)
{
    debug_log (tox4j_group_peerlist_update_cb, tox, group_number);
    auto msg = events.add_group_peerlist_update ();
	msg->set_group_number (group_number);
}

static void
tox4j_group_message_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, TOX_MESSAGE_TYPE type, const uint8_t *message, size_t length, Events &events)
{
    debug_log (tox4j_group_message_cb, tox, group_number, peer_number, type, message, length);
    auto msg = events.add_group_message ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
    set_message_type (msg, type);
	msg->set_message (message, length);
}

static void
tox4j_group_private_message_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, const uint8_t *message, size_t length, Events &events)
{
    debug_log (tox4j_group_private_message_cb, tox, group_number, peer_number, message, length);
    auto msg = events.add_group_private_message ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
	msg->set_message (message, length);
}

static void
tox4j_group_invite_cb (Tox *tox, int32_t friend_number, const uint8_t *invite_data, size_t length, Events &events)
{
    debug_log (tox4j_group_invite_cb, tox, friend_number, invite_data, length);
    auto msg = events.add_group_invite ();
	msg->set_friend_number (friend_number);
	msg->set_invite_data (invite_data, length);
}

static void
tox4j_group_peer_join_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, Events &events)
{
    debug_log (tox4j_group_peer_join_cb, tox, group_number, peer_number);
    auto msg = events.add_group_peer_join ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
}

static void
tox4j_group_peer_exit_cb (Tox *tox, uint32_t group_number, uint32_t peer_number, const uint8_t *part_message, size_t length, Events &events)
{
    debug_log (tox4j_group_peer_exit_cb, tox, group_number, peer_number, part_message, length);
    auto msg = events.add_group_peer_exit ();
	msg->set_group_number (group_number);
	msg->set_peer_number (peer_number);
	msg->set_part_message (part_message, length);
}

static void
tox4j_group_self_join_cb (Tox *tox, uint32_t group_number, Events &events)
{
    debug_log (tox4j_group_self_join_cb, tox, group_number);
    auto msg = events.add_group_self_join ();
	msg->set_group_number (group_number);
}

static void
tox4j_group_join_fail_cb (Tox *tox, uint32_t group_number, TOX_GROUP_JOIN_FAIL type, Events &events)
{
    debug_log (tox4j_group_join_fail_cb, tox, group_number, type);
    auto msg = events.add_group_join_fail ();
	msg->set_group_number (group_number);

	using proto::JoinFail;
	switch (type)
  	  {
  	  case TOX_GROUP_JOIN_FAIL_NAME_TAKEN:
  	    msg->set_type (JoinFail::NAME_TAKEN);
  	    break;
  	  case TOX_GROUP_JOIN_FAIL_PEER_LIMIT:
  	    msg->set_type (JoinFail::PEER_LIMIT);
        break;
  	  case TOX_GROUP_JOIN_FAIL_INVALID_PASSWORD:
  	    msg->set_type (JoinFail::INVALID_PASSWORD);
        break;
  	  case TOX_GROUP_JOIN_FAIL_UNKNOWN:
  	    msg->set_type (JoinFail::UNKNOWN);
        break;
	  }
}

static void
tox4j_group_moderation_cb (Tox *tox, uint32_t group_number, uint32_t source_peernum, uint32_t target_peernum, TOX_GROUP_MOD_EVENT type, Events &events)
{
    debug_log (tox4j_group_moderation_cb, tox, group_number, source_peernum, target_peernum, type);
    auto msg = events.add_group_moderation ();
	msg->set_group_number (group_number);
	msg->set_source_peer_number (source_peernum);
	msg->set_target_peer_number (target_peernum);

	using proto::GroupModEvent;
	switch (type)
	  {
	  case TOX_GROUP_MOD_EVENT_KICK:
	    msg->set_type(GroupModEvent::KICK);
	    break;
	  case TOX_GROUP_MOD_EVENT_BAN:
	    msg->set_type(GroupModEvent::BAN);
      	break;
	  case TOX_GROUP_MOD_EVENT_OBSERVER:
	    msg->set_type(GroupModEvent::OBSERVER);
      	break;
	  case TOX_GROUP_MOD_EVENT_USER:
	    msg->set_type(GroupModEvent::USER);
      	break;
	  case TOX_GROUP_MOD_EVENT_MODERATOR:
	    msg->set_type(GroupModEvent::MODERATOR);
      	break;
	  }
}

static auto
tox_options_new_unique ()
{
  struct Tox_Options_Deleter
  {
    void operator () (Tox_Options *options)
    {
      tox_options_free (options);
    }
  };

  return std::unique_ptr<Tox_Options, Tox_Options_Deleter> (tox_options_new (nullptr));
}


static tox::core_ptr
tox_new_unique (Tox_Options const *options, TOX_ERR_NEW *error)
{
  return tox::core_ptr (tox_new (options, error));
}

register_funcs (
  register_func (tox_new_unique)
);


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxNew
 * Signature: (ZZILjava/lang/String;IIII)I
 */
TOX_METHOD (jint, New,
  jboolean ipv6Enabled, jboolean udpEnabled,
  jint proxyType, jstring proxyHost, jint proxyPort,
  jint startPort, jint endPort, jint tcpPort,
  jint saveDataType, jbyteArray saveData)
{
#if 0
  scope_guard {
    [&]{ printf ("creating new instance"); },
  };
#endif

  auto opts = tox_options_new_unique ();
  if (!opts)
    {
      throw_tox_exception<Tox> (env, TOX_ERR_NEW_MALLOC);
      return 0;
    }

  opts->ipv6_enabled = ipv6Enabled;
  opts->udp_enabled = udpEnabled;

  opts->proxy_type = [=] {
    switch (proxyType)
      {
      case 0: return TOX_PROXY_TYPE_NONE;
      case 1: return TOX_PROXY_TYPE_HTTP;
      case 2: return TOX_PROXY_TYPE_SOCKS5;
      }
    tox4j_fatal ("Invalid proxy type from Java");
  } ();
  UTFChars proxy_host (env, proxyHost);
  opts->proxy_host = proxy_host.data ();
  opts->proxy_port = proxyPort;

  opts->start_port = startPort;
  opts->end_port = endPort;
  opts->tcp_port = tcpPort;

  auto assert_valid_uint16 = [env](int port) {
    tox4j_assert (port >= 0);
    tox4j_assert (port <= 65535);
  };
  if (opts->proxy_type != TOX_PROXY_TYPE_NONE)
    assert_valid_uint16 (proxyPort);
  assert_valid_uint16 (startPort);
  assert_valid_uint16 (endPort);
  assert_valid_uint16 (tcpPort);

  ByteArray save_data (env, saveData);
  opts->savedata_type = [=] {
    switch (saveDataType)
      {
      case 0: return TOX_SAVEDATA_TYPE_NONE;
      case 1: return TOX_SAVEDATA_TYPE_TOX_SAVE;
      case 2: return TOX_SAVEDATA_TYPE_SECRET_KEY;
      }
    tox4j_fatal ("Invalid savedata type type from Java");
  } ();
  opts->savedata_data   = save_data.data ();
  opts->savedata_length = save_data.size ();

  return instances.with_error_handling (env,
    [env] (tox::core_ptr tox)
      {
        tox4j_assert (tox != nullptr);

        register_funcs (
          register_func (tox4j_self_connection_status_cb  ),
          register_func (tox4j_friend_name_cb             ),
          register_func (tox4j_friend_status_message_cb   ),
          register_func (tox4j_friend_status_cb           ),
          register_func (tox4j_friend_connection_status_cb),
          register_func (tox4j_friend_typing_cb           ),
          register_func (tox4j_friend_read_receipt_cb     ),
          register_func (tox4j_friend_request_cb          ),
          register_func (tox4j_friend_message_cb          ),
          register_func (tox4j_file_recv_cb               ),
          register_func (tox4j_file_recv_control_cb       ),
          register_func (tox4j_file_recv_chunk_cb         ),
          register_func (tox4j_file_chunk_request_cb      ),
          register_func (tox4j_friend_lossy_packet_cb     ),
          register_func (tox4j_friend_lossless_packet_cb  ),
          register_func (tox4j_group_peer_name_cb         ),
          register_func (tox4j_group_peer_status_cb       ),
          register_func (tox4j_group_topic_cb             ),
          register_func (tox4j_group_privacy_state_cb     ),
          register_func (tox4j_group_peer_limit_cb        ),
          register_func (tox4j_group_password_cb          ),
          register_func (tox4j_group_peerlist_update_cb   ),
          register_func (tox4j_group_message_cb           ),
          register_func (tox4j_group_private_message_cb   ),
          register_func (tox4j_group_invite_cb            ),
          register_func (tox4j_group_peer_join_cb         ),
          register_func (tox4j_group_peer_exit_cb         ),
          register_func (tox4j_group_self_join_cb         ),
          register_func (tox4j_group_join_fail_cb         ),
          register_func (tox4j_group_moderation_cb        )
        );

        // Create the master events object and set up our callbacks.
        auto events = tox::callbacks<Tox> (std::unique_ptr<Events> (new Events))
          .set<tox::callback_self_connection_status,    tox4j_self_connection_status_cb  > ()
          .set<tox::callback_friend_name,               tox4j_friend_name_cb             > ()
          .set<tox::callback_friend_status_message,     tox4j_friend_status_message_cb   > ()
          .set<tox::callback_friend_status,             tox4j_friend_status_cb           > ()
          .set<tox::callback_friend_connection_status,  tox4j_friend_connection_status_cb> ()
          .set<tox::callback_friend_typing,             tox4j_friend_typing_cb           > ()
          .set<tox::callback_friend_read_receipt,       tox4j_friend_read_receipt_cb     > ()
          .set<tox::callback_friend_request,            tox4j_friend_request_cb          > ()
          .set<tox::callback_friend_message,            tox4j_friend_message_cb          > ()
          .set<tox::callback_file_recv,                 tox4j_file_recv_cb               > ()
          .set<tox::callback_file_recv_control,         tox4j_file_recv_control_cb       > ()
          .set<tox::callback_file_recv_chunk,           tox4j_file_recv_chunk_cb         > ()
          .set<tox::callback_file_chunk_request,        tox4j_file_chunk_request_cb      > ()
          .set<tox::callback_friend_lossy_packet,       tox4j_friend_lossy_packet_cb     > ()
          .set<tox::callback_friend_lossless_packet,    tox4j_friend_lossless_packet_cb  > ()
          .set<tox::callback_group_peer_name,           tox4j_group_peer_name_cb         > ()
          .set<tox::callback_group_peer_status,         tox4j_group_peer_status_cb       > ()
          .set<tox::callback_group_topic,               tox4j_group_topic_cb             > ()
          .set<tox::callback_group_privacy_state,       tox4j_group_privacy_state_cb     > ()
          .set<tox::callback_group_peer_limit,          tox4j_group_peer_limit_cb        > ()
          .set<tox::callback_group_password,            tox4j_group_password_cb          > ()
          .set<tox::callback_group_peerlist_update,     tox4j_group_peerlist_update_cb   > ()
          .set<tox::callback_group_message,             tox4j_group_message_cb           > ()
          .set<tox::callback_group_private_message,     tox4j_group_private_message_cb   > ()
          .set<tox::callback_group_invite,              tox4j_group_invite_cb            > ()
          .set<tox::callback_group_peer_join,           tox4j_group_peer_join_cb         > ()
          .set<tox::callback_group_peer_exit,           tox4j_group_peer_exit_cb         > ()
          .set<tox::callback_group_self_join,           tox4j_group_self_join_cb         > ()
          .set<tox::callback_group_join_fail,           tox4j_group_join_fail_cb         > ()
          .set<tox::callback_group_moderation,          tox4j_group_moderation_cb        > ()
          .set (tox.get ());

        // We can create the new instance outside instance_manager's critical section.
        // This call locks the instance manager.
        return instances.add (
          env,
          std::move (tox),
          std::move (events)
        );
      },
    tox_new_unique, opts.get ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxKill
 * Signature: (I)I
 */
TOX_METHOD (void, Kill,
  jint instanceNumber)
{
  instances.kill (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFinalize
 * Signature: (I)V
 */
TOX_METHOD (void, Finalize,
  jint instanceNumber)
{
  instances.finalize (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGetSavedata
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, GetSavedata,
  jint instanceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_vector<uint8_t,
          tox_get_savedata_size,
          tox_get_savedata> (env, tox);
      }
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeSelfConnectionStatus
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeSelfConnectionStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint connection_status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_self_connection_status_cb (tox, (TOX_CONNECTION) connection_status, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecvControl
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecvControl
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jint control)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_file_recv_control_cb (tox, friend_number, file_number, (TOX_FILE_CONTROL) control, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecv
 * Signature: (IIIIJ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecv
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jint kind, jlong file_size, jbyteArray filename)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray filenameArray (env, filename);
        tox4j_file_recv_cb (tox, friend_number, file_number, kind, file_size, filenameArray.data (), filenameArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecvChunk
 * Signature: (IIIJ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecvChunk
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jlong position, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray dataArray (env, data);
        tox4j_file_recv_chunk_cb (tox, friend_number, file_number, position, dataArray.data (), dataArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileChunkRequest
 * Signature: (IIIJI)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileChunkRequest
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jlong position, jint length)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_file_chunk_request_cb (tox, friend_number, file_number, position, length, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendConnectionStatus
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendConnectionStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint connection_status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_friend_connection_status_cb (tox, friend_number, (TOX_CONNECTION) connection_status, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendLosslessPacket
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendLosslessPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray dataArray (env, data);
        tox4j_friend_lossless_packet_cb (tox, friend_number, dataArray.data (), dataArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendLossyPacket
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendLossyPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray dataArray (env, data);
        tox4j_friend_lossy_packet_cb (tox, friend_number, dataArray.data (), dataArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendMessage
 * Signature: (IIII[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint type, jint time_delta, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray messageArray (env, message);
        tox4j_friend_message_cb (tox, friend_number, (TOX_MESSAGE_TYPE) type, /*time_delta, */ messageArray.data (), messageArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendName
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendName
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray name)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray nameArray (env, name);
        tox4j_friend_name_cb (tox, friend_number, nameArray.data (), nameArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendRequest
 * Signature: (I[BI[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendRequest
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray public_key, jint time_delta, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray public_keyArray (env, public_key);
        ByteArray messageArray (env, message);
        tox4j_friend_request_cb (tox, public_keyArray.data (), /*time_delta, */ messageArray.data (), messageArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendStatus
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_friend_status_cb (tox, friend_number, (TOX_USER_STATUS) status, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendStatusMessage
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendStatusMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        ByteArray messageArray (env, message);
        tox4j_friend_status_message_cb (tox, friend_number, messageArray.data (), messageArray.size (), events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendTyping
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendTyping
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jboolean is_typing)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_friend_typing_cb (tox, friend_number, is_typing, events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendReadReceipt
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendReadReceipt
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint message_id)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        tox4j_friend_read_receipt_cb (tox, friend_number, message_id, events);
      }
  );
}
