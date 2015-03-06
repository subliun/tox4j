#include <tox/core.h>
#include <tox/core_uncompat.h>

#include <tox/tox.h>

#include <cassert>

#include <algorithm>
#include <fstream>
#include <map>
#include <vector>

#include "logging.h"

#pragma GCC diagnostic ignored "-Wunused-parameter"

#define DEBUG_CALLBACKS 0


#if DEBUG_CALLBACKS
struct log_stream
{
  log_stream ()
  {
    out.open ("tox.log", std::ofstream::out | std::ofstream::app);
  }

  ~log_stream ()
  {
    out << "\n";
  }

  std::ofstream out;
};

#undef LOG
#define LOG(severity) log_stream ().out


static int
id (void *p)
{
  static int counter = 0;
  static std::map<void *, int> ids;
  auto found = ids.find (p);
  if (found != ids.end ())
    return found->second;
  ids[p] = ++counter;
  return counter;
}


static char const *
string_of_control_type (uint8_t type)
{
  switch (type)
    {
    case TOX_FILECONTROL_ACCEPT       : return "TOX_FILECONTROL_ACCEPT";
    case TOX_FILECONTROL_PAUSE        : return "TOX_FILECONTROL_PAUSE";
    case TOX_FILECONTROL_KILL         : return "TOX_FILECONTROL_KILL";
    case TOX_FILECONTROL_FINISHED     : return "TOX_FILECONTROL_FINISHED";
    case TOX_FILECONTROL_RESUME_BROKEN: return "TOX_FILECONTROL_RESUME_BROKEN";
    default: return "<unknown control>";
    }
}
#endif


template<typename FuncT>
struct callback
{
  FuncT *func;
  void *user_data;
};


struct file_transfer
{
  enum pause_reason
  {
    INITIAL,
    SELF,
    FRIEND
  } cause;

  enum transfer_state
  {
    PAUSED,
    RUNNING,
    FINISHED
  } state = PAUSED;

  uint64_t position = 0;
  uint64_t file_size = 0;

  bool event_pending = false;
  size_t size_requested = 0;

  file_transfer () { }

  file_transfer (uint64_t file_size)
    : file_size (file_size)
  { }

  static uint32_t new_file_number (bool receive_send, uint8_t filenumber)
  {
    // File numbers are per-direction in the old API, but not in the new one.
    return (!receive_send << 8) | filenumber;
  }

  static uint8_t old_file_number (uint32_t file_number)
  {
    return file_number & 0xff;
  }

  static bool send_receive (uint32_t file_number)
  {
    return file_number & 0x100;
  }
};


struct new_Tox
{
  Tox *tox;
  bool connected = false;
  bool has_av = false;
  std::map<std::pair<uint32_t, uint32_t>, file_transfer> transfers;

  struct
  {
    callback<tox_connection_status_cb> connection_status;
    callback<tox_friend_name_cb> friend_name;
    callback<tox_friend_status_message_cb> friend_status_message;
    callback<tox_friend_status_cb> friend_status;
    callback<tox_friend_connection_status_cb> friend_connection_status;
    callback<tox_friend_typing_cb> friend_typing;
    callback<tox_read_receipt_cb> read_receipt;
    callback<tox_friend_request_cb> friend_request;
    callback<tox_friend_message_cb> friend_message;
    callback<tox_friend_action_cb> friend_action;
    callback<tox_file_control_cb> file_control;
    callback<tox_file_request_chunk_cb> file_request_chunk;
    callback<tox_file_receive_cb> file_receive;
    callback<tox_file_receive_chunk_cb> file_receive_chunk;
    callback<tox_group_invite_cb> group_invite;
    callback<tox_group_message_cb> group_message;
    callback<tox_group_private_message_cb> group_private_message;
    callback<tox_group_action_cb> group_action;
    callback<tox_group_nick_change_cb> group_nick_change;
    callback<tox_group_topic_change_cb> group_topic_change;
    callback<tox_group_peer_join_cb> group_peer_join;
    callback<tox_group_peer_exit_cb> group_peer_exit;
    callback<tox_group_self_join_cb> group_self_join;
    callback<tox_group_peerlist_update_cb> group_peerlist_update;
    callback<tox_group_self_timeout_cb> group_self_timeout;
    callback<tox_group_rejected_cb> group_rejected;
    callback<tox_friend_lossy_packet_cb> friend_lossy_packet;
    callback<tox_friend_lossless_packet_cb> friend_lossless_packet;
  } callbacks;

  struct CB
  {
    static void friend_request (Tox *tox, const uint8_t *public_key, const uint8_t *data, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB friend_request (#%d, %p, %p, %d)", id (tox), public_key, data, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_request;
      cb.func (self, public_key, data, length, cb.user_data);
    }

    static void friend_message (Tox *tox, int32_t friendnumber, const uint8_t * message, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB friend_message (#%d, %d, %p, %d)", id (tox), friendnumber, message, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_message;
      cb.func (self, friendnumber, message, length, cb.user_data);
    }

    static void friend_action (Tox *tox, int32_t friendnumber, const uint8_t * action, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB friend_action (#%d, %d, %p, %d)", id (tox), friendnumber, action, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_action;
      cb.func (self, friendnumber, action, length, cb.user_data);
    }

    static void name_change (Tox *tox, int32_t friendnumber, const uint8_t *newname, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB name_change (#%d, %d, %p, %d)", id (tox), friendnumber, newname, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_name;
      if (length == 1 && newname[0] == '\0')
        cb.func (self, friendnumber, nullptr, 0, cb.user_data);
      else
        cb.func (self, friendnumber, newname, length, cb.user_data);
    }

    static void status_message (Tox *tox, int32_t friendnumber, const uint8_t *newstatus, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB status_message (#%d, %d, %p, %d)", id (tox), friendnumber, newstatus, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_status_message;
      if (length == 1 && newstatus[0] == '\0')
        cb.func (self, friendnumber, nullptr, 0, cb.user_data);
      else
        cb.func (self, friendnumber, newstatus, length, cb.user_data);
    }

    static void user_status (Tox *tox, int32_t friendnumber, uint8_t TOX_USERSTATUS, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB user_status (#%d, %d, %d)", id (tox), friendnumber, TOX_USERSTATUS);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_status;
      cb.func (self, friendnumber, (TOX_STATUS) TOX_USERSTATUS, cb.user_data);
    }

    static void typing_change (Tox *tox, int32_t friendnumber, uint8_t is_typing, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB typing_change (#%d, %d, %d)", id (tox), friendnumber, is_typing);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_typing;
      cb.func (self, friendnumber, is_typing, cb.user_data);
    }

    static void read_receipt (Tox *tox, int32_t friendnumber, uint32_t receipt, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB read_receipt (#%d, %d, %d)", id (tox), friendnumber, receipt);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.read_receipt;
      cb.func (self, friendnumber, receipt, cb.user_data);
    }

    static void connection_status (Tox *tox, int32_t friendnumber, uint8_t status, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB connection_status (#%d, %d, %d)", id (tox), friendnumber, status);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_connection_status;
      cb.func (self, friendnumber, status ? TOX_CONNECTION_UDP4 : TOX_CONNECTION_NONE, cb.user_data);
    }

    static void file_send_request (Tox *tox, int32_t friendnumber, uint8_t filenumber, uint64_t filesize, const uint8_t *filename, uint16_t filename_length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB file_send_request (#%d, %d, %d, %ld, %p, %d)", id (tox), friendnumber, filenumber, filesize, filename, filename_length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.file_receive;

      self->add_transfer (friendnumber, filenumber | 0x100, filesize);

      // XXX: it's always DATA. We could break protocol and send it in one of
      // the filesize bits, but then we would no longer be able to send to old
      // clients (receiving would work). Also, toxcore might not like it (I
      // don't know whether it interprets filesize).
      cb.func (self, friendnumber, filenumber | 0x100, TOX_FILE_KIND_DATA, filesize, filename, filename_length, cb.user_data);
    }

    static void file_control (Tox *tox, int32_t friendnumber, uint8_t receive_send, uint8_t filenumber, uint8_t control_type, const uint8_t *data, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB file_control (#%d, %d, %d, %d, %s, %p, %d)", id (tox), friendnumber, receive_send, filenumber, string_of_control_type (control_type), data, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);

      uint32_t file_number = file_transfer::new_file_number (receive_send, filenumber);
      file_transfer *transfer = self->get_transfer (friendnumber, file_number);
      assert (transfer != nullptr);

      TOX_FILE_CONTROL control;
      switch (control_type)
        {
        case TOX_FILECONTROL_ACCEPT:
          control = TOX_FILE_CONTROL_RESUME;
          break;
        case TOX_FILECONTROL_PAUSE:
          assert (false);
          control = TOX_FILE_CONTROL_PAUSE;
          break;
        case TOX_FILECONTROL_KILL:
          assert (false);
          control = TOX_FILE_CONTROL_PAUSE;
          break;
        case TOX_FILECONTROL_FINISHED:
          {
            // We're done, send a request for 0 bytes to let the client know.
            auto cb = self->callbacks.file_request_chunk;
            cb.func (self, friendnumber, file_number, transfer->position,
                     0, cb.user_data);
            // Then delete the transfer state.
            self->remove_transfer (friendnumber, file_number);
            // Nothing more to do.
            return;
          }
        case TOX_FILECONTROL_RESUME_BROKEN:
          assert (false);
          control = TOX_FILE_CONTROL_PAUSE;
          break;
        default:
          assert (false);
        }

      switch (control)
        {
        case TOX_FILE_CONTROL_PAUSE:
          assert (false);
          break;
        case TOX_FILE_CONTROL_RESUME:
          transfer->state = file_transfer::RUNNING;
          break;
        case TOX_FILE_CONTROL_CANCEL:
          assert (false);
          break;
        }

      auto cb = self->callbacks.file_control;
      cb.func (self, friendnumber, file_number, control, cb.user_data);
    }

    static void file_data (Tox *tox, int32_t friendnumber, uint8_t filenumber, const uint8_t *data, uint16_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB file_data (#%d, %d, %d, %p, %d)", id (tox), friendnumber, filenumber, data, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);

      file_transfer *transfer = self->get_transfer (friendnumber, filenumber | 0x100);
      assert (transfer != nullptr);

      auto cb = self->callbacks.file_receive_chunk;
      cb.func (self, friendnumber, filenumber | 0x100, transfer->position, data, length, cb.user_data);

      transfer->position += length;

      if (transfer->position == transfer->file_size)
        {
          int result = tox_file_send_control (tox, friendnumber, 1, filenumber, TOX_FILECONTROL_FINISHED, nullptr, 0);
          assert (result == 0);
        }
    }

    static void group_invite (Tox *m, int32_t friendnumber, const uint8_t *invite_data, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_invite;
      cb.func (self, friendnumber, invite_data, length, userdata);
    }

    static void group_message (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_message;
      cb.func (self, groupnumber, peernumber, message, length, cb.user_data);
    }

    static void group_private_message (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_private_message;
      cb.func (self, groupnumber, peernumber, message, length, cb.user_data);
    }

    static void group_action (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_action;
      cb.func (self, groupnumber, peernumber, message, length, cb.user_data);
    }

    static void group_nick_change (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *newnick, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_nick_change;
      cb.func (self, groupnumber, peernumber, newnick, length, cb.user_data);
    }

    static void group_topic_change (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *topic, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_topic_change;
      cb.func (self, groupnumber, peernumber, topic, length, cb.user_data);
    }

    static void group_peer_join (Tox *m, int groupnumber, uint32_t peernumber, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_peer_join;
      cb.func (self, groupnumber, peernumber, cb.user_data);
    }

    static void group_peer_exit (Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *partmessage, uint16_t length, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_peer_exit;
      cb.func (self, groupnumber, peernumber, partmessage, length, cb.user_data);
    }

    static void group_self_join (Tox *m, int groupnumber, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_self_join;
      cb.func (self, groupnumber, cb.user_data);
    }

    static void group_peerlist_update (Tox *m, int groupnumber, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_peerlist_update;
      cb.func (self, groupnumber, cb.user_data);
    }

    static void group_self_timeout (Tox *m, int groupnumber, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_self_timeout;
      cb.func (self, groupnumber, cb.user_data);
    }

    static void group_rejected (Tox *m, int groupnumber, uint8_t type, void *userdata)
    {
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.group_rejected;
      cb.func (self, groupnumber, type, cb.user_data);
    }

    static int lossy_packet (Tox *tox, int32_t friendnumber, const uint8_t *data, uint32_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB lossy_packet (#%d, %d, %p, %d)", id (tox), friendnumber, data, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_lossy_packet;
      cb.func (self, friendnumber, data, length, cb.user_data);
      return 0;
    }

    static int lossless_packet (Tox *tox, int32_t friendnumber, const uint8_t *data, uint32_t length, void *userdata)
    {
#if DEBUG_CALLBACKS
      LOG (INFO) << lwt::format ("CB lossless_packet (#%d, %d, %p, %d)", id (tox), friendnumber, data, length);
#endif
      auto self = static_cast<new_Tox *> (userdata);
      auto cb = self->callbacks.friend_lossless_packet;
      cb.func (self, friendnumber, data, length, cb.user_data);
      return 0;
    }
  };

  new_Tox (Tox *tox)
    : tox (tox)
  {
    tox_callback_friend_request         (tox, CB::friend_request       , this);
    tox_callback_friend_message         (tox, CB::friend_message       , this);
    tox_callback_friend_action          (tox, CB::friend_action        , this);
    tox_callback_name_change            (tox, CB::name_change          , this);
    tox_callback_status_message         (tox, CB::status_message       , this);
    tox_callback_user_status            (tox, CB::user_status          , this);
    tox_callback_typing_change          (tox, CB::typing_change        , this);
    tox_callback_read_receipt           (tox, CB::read_receipt         , this);
    tox_callback_connection_status      (tox, CB::connection_status    , this);
    tox_callback_file_send_request      (tox, CB::file_send_request    , this);
    tox_callback_group_invite           (tox, CB::group_invite         , this);
    tox_callback_group_message          (tox, CB::group_message        , this);
    tox_callback_group_private_message  (tox, CB::group_private_message, this);
    tox_callback_group_action           (tox, CB::group_action         , this);
    tox_callback_group_nick_change      (tox, CB::group_nick_change    , this);
    tox_callback_group_topic_change     (tox, CB::group_topic_change   , this);
    tox_callback_group_peer_join        (tox, CB::group_peer_join      , this);
    tox_callback_group_peer_exit        (tox, CB::group_peer_exit      , this);
    tox_callback_group_self_join        (tox, CB::group_self_join      , this);
    tox_callback_group_peerlist_update  (tox, CB::group_peerlist_update, this);
    tox_callback_group_self_timeout     (tox, CB::group_self_timeout   , this);
    tox_callback_group_rejected         (tox, CB::group_rejected       , this);
    tox_callback_file_control           (tox, CB::file_control         , this);
    tox_callback_file_data              (tox, CB::file_data            , this);
  }

  void register_custom_packet_handlers (uint32_t friend_number)
  {
    for (uint8_t byte = 200; byte <= 254; byte++)
      tox_lossy_packet_registerhandler (tox, friend_number, byte, CB::lossy_packet, this);
    for (uint8_t byte = 160; byte <= 191; byte++)
      tox_lossless_packet_registerhandler (tox, friend_number, byte, CB::lossless_packet, this);
  }

  void add_transfer (uint32_t friend_number, uint32_t file_number, uint64_t file_size)
  {
    assert (!get_transfer (friend_number, file_number));
    transfers[std::make_pair (friend_number, file_number)] = file_transfer (file_size);
  }

  file_transfer *get_transfer (uint32_t friend_number, uint32_t file_number)
  {
    auto found = transfers.find (std::make_pair (friend_number, file_number));
    if (found == transfers.end ())
      return nullptr;
    return &found->second;
  }

  void remove_transfer (uint32_t friend_number, uint32_t file_number)
  {
    auto found = transfers.find (std::make_pair (friend_number, file_number));
    assert (found != transfers.end ());
    transfers.erase (found);
  }
};
