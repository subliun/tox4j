#pragma once

#include "tox/common.h"
#include <tox/tox.h>


namespace tox
{
  struct core_deleter
  {
    void operator () (Tox *tox)
    {
      tox_kill (tox);
    }
  };

  typedef std::unique_ptr<Tox, core_deleter> core_ptr;

#define CALLBACK(NAME)  using callback_##NAME = detail::cb<Tox, tox_##NAME##_cb, tox_callback_##NAME>
  CALLBACK (self_connection_status);
  CALLBACK (friend_name);
  CALLBACK (friend_status_message);
  CALLBACK (friend_status);
  CALLBACK (friend_connection_status);
  CALLBACK (friend_typing);
  CALLBACK (friend_read_receipt);
  CALLBACK (friend_request);
  CALLBACK (friend_message);
  CALLBACK (file_recv_control);
  CALLBACK (file_chunk_request);
  CALLBACK (file_recv);
  CALLBACK (file_recv_chunk);
  CALLBACK (friend_lossy_packet);
  CALLBACK (friend_lossless_packet);
  CALLBACK (group_peer_name);
  CALLBACK (group_peer_status);
  CALLBACK (group_topic);
  CALLBACK (group_privacy_state);
  CALLBACK (group_peer_limit);
  CALLBACK (group_password);
  CALLBACK (group_peerlist_update);
  CALLBACK (group_message);
  CALLBACK (group_private_message);
  CALLBACK (group_invite);
  CALLBACK (group_peer_join);
  CALLBACK (group_peer_exit);
  CALLBACK (group_self_join);
  CALLBACK (group_join_fail);
  CALLBACK (group_moderation);
#undef CALLBACK
}
