#pragma once

#include <tox/tox.h>

#include <memory>
#include <cassert>

typedef enum TOX_ERR_GROUP_JOIN {
  TOX_ERR_GROUP_JOIN_OK,
  TOX_ERR_GROUP_JOIN_FAILED
} TOX_ERR_GROUP_JOIN;

/* Adds a new groupchat to group chats array.
* group_name is required and length must not exceed TOX_MAX_GROUP_NAME_LENGTH bytes.
*
* Return groupnumber on success.
* Return -1 on failure.
*/
int new_tox_group_new(Tox *tox, const uint8_t *group_name, uint16_t length, TOX_ERR_GROUP_JOIN *error);

/* Creates and joins a groupchat using the supplied public key.
*
* Return groupnumber on success.
* Return -1 on failure.
*/
int new_tox_group_new_join(Tox *tox, const uint8_t *invite_key, TOX_ERR_GROUP_JOIN *error);

/* Reconnects to groupnumber's group and maintains your own state, i.e. status, keys, certificates
*
* Return groupnumber on success.
* Return -1 on failure or if already connected to the group.
*/
int new_tox_group_reconnect(Tox *tox, int groupnumber, TOX_ERR_GROUP_JOIN *error);

/* Joins a group using the invite data received in a friend's group invite.
*
* Return groupnumber on success.
* Return -1 on failure
*/
int new_tox_group_accept_invite(Tox *tox, const uint8_t *invite_data, uint16_t length, TOX_ERR_GROUP_JOIN *error);

/* Invites friendnumber to groupnumber.
*
* Return 0 on success.
* Return -1 on failure.
*/
void new_tox_group_invite_friend(Tox *tox, int groupnumber, int32_t friendnumber);

typedef enum TOX_ERR_GROUP_DELETE {
  TOX_ERR_GROUP_DELETE_OK,
  TOX_ERR_GROUP_DELETE_FAILED,
} TOX_ERR_GROUP_DELETE;

/* Deletes groupnumber's group chat and sends an optional parting message to group peers
* The maximum parting message length is TOX_MAX_GROUP_PART_LENGTH.
*
* Return true on success.
*/
bool new_tox_group_delete(Tox *tox, int groupnumber, const uint8_t *partmessage, uint16_t length, TOX_ERR_GROUP_DELETE *error);

typedef enum TOX_ERR_GROUP_SEND {
  TOX_ERR_GROUP_SEND_OK,
  TOX_ERR_GROUP_SEND_FAILED
} TOX_ERR_GROUP_SEND;

/* Sends a groupchat message to groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
*
* Return 0 on success.
* Return -1 on failure.
*/
bool new_tox_group_message_send(const Tox *tox, int groupnumber, const uint8_t *message, uint16_t length, TOX_ERR_GROUP_SEND *error);

/* Sends a private message to peernumber in groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
*
* Return 0 on success.
* Return -1 on failure.
*/
bool new_tox_group_private_message_send(const Tox *tox, int groupnumber, uint32_t peernumber, const uint8_t *message,
uint16_t length, TOX_ERR_GROUP_SEND *error);

/* Sends a groupchat action message to groupnumber. Messages should be split at TOX_MAX_MESSAGE_LENGTH bytes.
*
* Return 0 on success.
* Return -1 on failure.
*/
bool new_tox_group_action_send(const Tox *tox, int groupnumber, const uint8_t *message, uint16_t length, TOX_ERR_GROUP_SEND *error);

typedef enum TOX_ERR_GROUP_SET_NAME {
  TOX_ERR_GROUP_SET_NAME_OK,
  TOX_ERR_GROUP_SET_NAME_FAILED,
  TOX_ERR_GROUP_SET_NAME_TAKEN
} TOX_ERR_GROUP_SET_NAME;

/* Sets your name for groupnumber. length should be no larger than TOX_MAX_NAME_LENGTH bytes.
*
* Return 0 on success.
* Return -1 on failure.
* Return -2 if nick is already taken by another group member
*/
bool new_tox_group_set_self_name(Tox *tox, int groupnumber, const uint8_t *name, uint16_t length, TOX_ERR_GROUP_SET_NAME *error);

typedef enum TOX_ERR_GROUP_QUERY {
  TOX_ERR_GROUP_QUERY_OK,
  TOX_ERR_GROUP_QUERY_FAILED
} TOX_ERR_GROUP_QUERY;

size_t new_tox_group_get_peer_name_size (Tox const *tox, int groupnumber, uint32_t peernumber);

/* Get peernumber's name in groupnumber's group chat.
* name buffer must be at least TOX_MAX_NAME_LENGTH bytes.
*
* Return length of name on success.
* Reutrn -1 on failure.
*/
bool new_tox_group_get_peer_name(const Tox *tox, int groupnumber, uint32_t peernumber, uint8_t *name);

size_t new_tox_group_get_self_name_size (Tox const *tox, int groupnumber);

/* Get your own name for groupnumber's group.
* name buffer must be at least TOX_MAX_NAME_LENGTH bytes.
*
* Return length of name on success.
* Reutrn -1 on failure.
*/
bool new_tox_group_get_self_name(const Tox *tox, int groupnumber, uint8_t *name);

typedef enum TOX_ERR_GROUP_SET {
  TOX_ERR_GROUP_SET_OK,
  TOX_ERR_GROUP_SET_FAILED
} TOX_ERR_GROUP_SET;

/* Changes groupnumber's topic.
*/
bool new_tox_group_set_topic(Tox *tox, int groupnumber, const uint8_t *topic, uint16_t length, TOX_ERR_GROUP_SET *error);

size_t new_tox_group_get_topic_size (Tox const *tox, int groupnumber);

/* Gets groupnumber's topic. topic buffer must be at least TOX_MAX_GROUP_TOPIC_LENGTH bytes.
*
* Return topic length on success.
* Return -1 on failure.
*/
bool new_tox_group_get_topic(const Tox *tox, int groupnumber, uint8_t *topic);

size_t new_tox_group_get_group_name_size (Tox const *tox, int groupnumber);

/* Gets groupnumber's group name. groupname buffer must be at least TOX_MAX_GROUP_NAME_LENGTH bytes.
*/
bool new_tox_group_get_group_name(const Tox *tox, int groupnumber, uint8_t *groupname);

/* Sets your status for groupnumber.
*/
bool new_tox_group_set_status(Tox *tox, int groupnumber, uint8_t status_type, TOX_ERR_GROUP_SET *error);

/* Get peernumber's status in groupnumber's group chat.
*
* Returns a TOX_GROUP_STATUS on success.
* Returns TOX_GS_INVALID on failure.
*/
TOX_GROUP_STATUS new_tox_group_get_status(const Tox *tox, int groupnumber, uint32_t peernumber);

/* Get peernumber's group role in groupnumber's group chat.
*
* Returns a TOX_GROUP_ROLE on success.
* Returns TOX_GR_INVALID on failure.
*/
TOX_GROUP_ROLE new_tox_group_get_role(const Tox *tox, int groupnumber, uint32_t peernumber);

/* Get invite key for the groupchat from groupnumber.
* The result is stored in 'dest' which must have space for TOX_GROUP_CHAT_ID_SIZE bytes.
*/
bool new_tox_group_get_chat_id(const Tox *tox, int groupnumber, uint8_t *dest, TOX_ERR_GROUP_QUERY *error);

/* Copies the nicks of the peers in groupnumber to the nicks array.
* Copies the lengths of the nicks to the lengths array.
*
* Arrays must have room for num_peers items.
*
* Should be used with tox_callback_group_peerlist_update.
*
* returns number of peers on success.
* return -1 on failure.
*/
int new_tox_group_get_names(const Tox *tox, int groupnumber, uint8_t nicks[][TOX_MAX_NAME_LENGTH], uint16_t lengths[],
uint32_t num_peers, TOX_ERR_GROUP_QUERY *error);

/* Returns the number of peers in groupnumber on success.
*
* Returns -1 on failure.
*/
int new_tox_group_get_number_peers(const Tox *tox, int groupnumber, TOX_ERR_GROUP_QUERY *error);

/* Returns the number of active groups. */
uint32_t new_tox_group_count_groups(const Tox *tox);

/* Toggle ignore on peernumber in groupnumber.
* If ignore is 1, group and private messages from peernumber are ignored, as well as A/V.
* If ignore is 0, peer is unignored.
*
* Return 0 on success.
* Return -1 on failure.
*/
int new_tox_group_toggle_ignore(Tox *tox, int groupnumber, uint32_t peernumber, uint8_t ignore, TOX_ERR_GROUP_SET *error);

typedef void tox_group_invite_cb(Tox *tox, int32_t friendnumber, const uint8_t *invite_data, uint16_t length, void *userdata);

/* Set the callback for group invites from friends.
*
* function(Tox *m, int32_t friendnumber, const uint8_t *invite_data, uint16_t length, void *userdata)
*/
void tox_callback_group_invite(Tox *tox, tox_group_invite_cb *function, void *userdata);


typedef void tox_group_message_cb(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata);

/* Set the callback for group messages.
 *
 *  function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
 */
void tox_callback_group_message(Tox *tox, tox_group_message_cb *function, void *userdata);


typedef void tox_group_private_message_cb(Tox *tox, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata);
/* Set the callback for group private messages.
*
* function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
*/
void tox_callback_group_private_message(Tox *tox, tox_group_private_message_cb *function, void *userdata);


typedef void tox_group_action_cb(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata);
/* Set the callback for group action messages (aka /me messages).
*
* function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *message, uint16_t length, void *userdata)
*/
void tox_callback_group_action(Tox *tox, tox_group_action_cb *function, void *userdata);


typedef void tox_group_nick_change_cb(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *newnick, uint16_t length, void *userdata);
/* Set the callback for group peer nickname changes.
*
* function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *newnick, uint16_t length, void *userdata)
*/
void tox_callback_group_nick_change(Tox *tox, tox_group_nick_change_cb *function, void *userdata);


typedef void tox_group_topic_change_cb(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *topic, uint16_t length, void *userdata);
/* Set the callback for group topic changes.
*
* function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *topic, uint16_t length, void *userdata)
*/
void tox_callback_group_topic_change(Tox *tox, tox_group_topic_change_cb *function, void *userdata);


typedef void tox_group_peer_join_cb(Tox *m, int groupnumber, uint32_t peernumber, void *userdata);
/* Set the callback for group peer join.
*
* function(Tox *m, int groupnumber, uint32_t peernumber, void *userdata)
*/
void tox_callback_group_peer_join(Tox *tox, tox_group_peer_join_cb *function, void *userdata);


typedef void tox_group_peer_exit_cb(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *partmessage, uint16_t length, void *userdata);
/* Set the callback for group peer exit.
*
* function(Tox *m, int groupnumber, uint32_t peernumber, const uint8_t *partmessage, uint16_t length, void *userdata)
*/
void tox_callback_group_peer_exit(Tox *tox, tox_group_peer_exit_cb *function, void *userdata);


typedef void tox_group_self_join_cb(Tox *m, int groupnumber, void *userdata);
/* Set the callback for group self join.
*
* function(Tox *m, int groupnumber, void *userdata)
*/
void tox_callback_group_self_join(Tox *tox, tox_group_self_join_cb *function, void *userdata);


typedef void tox_group_peerlist_update_cb(Tox *m, int groupnumber, void *userdata);
/* Set the callback for peerlist update. Should be used with tox_group_get_names.
*
* function(Tox *m, int groupnumber, void *userdata)
*/
void tox_callback_group_peerlist_update(Tox *tox, tox_group_peerlist_update_cb *function, void *userdata);

typedef void tox_group_rejected_cb(Tox *m, int groupnumber, uint8_t type, void *userdata);
/* Set the callback for when your join attempt is rejected where type is one of TOX_GROUP_JOIN_REJECTED.
*
* function(Tox *m, int groupnumber, uint8_t type, void *userdata)
*/
void tox_callback_group_rejected(Tox *tox, tox_group_rejected_cb *function, void *userdata);

namespace tox
{

  namespace detail
  {

    template<typename ...Args>
    struct tox_cb
    {
      template<void Set (Tox *, void (*)(Tox *, Args..., void *), void *)>
      struct func
      {
        template<typename UserData>
        using type = void (Tox *, Args..., UserData &);

        template<typename UserData, type<UserData> Callback>
        static void
        invoke (Tox *tox, Args ...args, void *user_data)
        {
          Callback (tox, args..., *static_cast<UserData *> (user_data));
        }

        template<typename UserData, type<UserData> Callback>
        static std::unique_ptr<UserData>
        set (Tox *tox, std::unique_ptr<UserData> user_data)
        {
          assert (user_data.get () != nullptr);
          Set (tox, invoke<UserData, Callback>, user_data.get ());
          return user_data;
        }
      };
    };


    template<typename Sig>
    struct mk_tox_cb;

    // XXX: why don't variadic templates work here?
    template<typename ...Args>
    struct mk_tox_cb<void (Tox *, Args..., void *)>
      : tox_cb<Args...>
    { };

    template<typename Arg1>
    struct mk_tox_cb<void (Tox *, Arg1, void *)>
      : tox_cb<Arg1>
    { };

    template<typename Arg1, typename Arg2>
    struct mk_tox_cb<void (Tox *, Arg1, Arg2, void *)>
      : tox_cb<Arg1, Arg2>
    { };

    template<typename Arg1, typename Arg2, typename Arg3>
    struct mk_tox_cb<void (Tox *, Arg1, Arg2, Arg3, void *)>
      : tox_cb<Arg1, Arg2, Arg3>
    { };

    template<typename Arg1, typename Arg2, typename Arg3, typename Arg4>
    struct mk_tox_cb<void (Tox *, Arg1, Arg2, Arg3, Arg4, void *)>
      : tox_cb<Arg1, Arg2, Arg3, Arg4>
    { };

    template<typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5>
    struct mk_tox_cb<void (Tox *, Arg1, Arg2, Arg3, Arg4, Arg5, void *)>
      : tox_cb<Arg1, Arg2, Arg3, Arg4, Arg5>
    { };

    template<typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6>
    struct mk_tox_cb<void (Tox *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, void *)>
      : tox_cb<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6>
    { };


    template<typename Sig, void Set (Tox *, Sig, void *)>
    struct cb
      : mk_tox_cb<Sig>::template func<Set>
    { };

  }

#define CALLBACK(NAME)  using callback_##NAME = detail::cb<tox_##NAME##_cb, tox_callback_##NAME>
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
  CALLBACK (group_invite);
  CALLBACK (group_message);
  CALLBACK (group_private_message);
  CALLBACK (group_action);
  CALLBACK (group_nick_change);
  CALLBACK (group_topic_change);
  CALLBACK (group_peer_join);
  CALLBACK (group_peer_exit);
  CALLBACK (group_self_join);
  CALLBACK (group_peerlist_update);
  CALLBACK (group_rejected);
  CALLBACK (friend_lossy_packet);
  CALLBACK (friend_lossless_packet);
#undef CALLBACK


  namespace detail
  {

    template<
      typename UserData,
      typename Cb,
      typename Cb::template type<UserData> Sig
    >
    struct setter
    {
      static std::unique_ptr<UserData>
      set (Tox *tox, std::unique_ptr<UserData> user_data)
      {
        return Cb::template set<UserData, Sig> (tox, std::move (user_data));
      }
    };


    template<typename UserData, typename ...Callbacks>
    struct set_callbacks;

    template<typename UserData>
    struct set_callbacks<UserData>
    {
      static std::unique_ptr<UserData>
      set (Tox *, std::unique_ptr<UserData> user_data)
      {
        return user_data;
      }
    };

    template<typename UserData, typename Head, typename ...Tail>
    struct set_callbacks<UserData, Head, Tail...>
    {
      static std::unique_ptr<UserData>
      set (Tox *tox, std::unique_ptr<UserData> user_data)
      {
        return set_callbacks<UserData, Tail...>::set (tox, Head::set (tox, std::move (user_data)));
      }
    };


    template<typename UserData, typename ...Callbacks>
    struct callback_setter
    {
      template<
        typename Cb,
        typename Cb::template type<UserData> Sig
      >
      callback_setter<UserData, Callbacks..., setter<UserData, Cb, Sig>>
      set () &&
      { return { std::move (user_data) }; }

      std::unique_ptr<UserData>
      set (Tox *tox) &&
      {
        return set_callbacks<UserData, Callbacks...>::set (tox, std::move (user_data));
      }

      std::unique_ptr<UserData> user_data;
    };

  }


  template<typename UserData>
  detail::callback_setter<UserData>
  callbacks (std::unique_ptr<UserData> user_data)
  {
    return { std::move (user_data) };
  }

}
