#include "ToxCore.h"

int
new_tox_group_accept_invite(Tox *tox, const uint8_t *invite_data, uint16_t length, TOX_ERR_GROUP_JOIN *error)
{
  int group_number = tox_group_accept_invite(tox, invite_data, length);
  if (group_number < 0) {
    *error = TOX_ERR_GROUP_JOIN_OK;
  }

  if (error) *error = TOX_ERR_GROUP_JOIN_OK;
  return group_number;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupAcceptInvite
 * Signature: (I[B)
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupAcceptInvite
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray inviteData)
{
    ByteArray inviteDataArray(env, inviteData);
    return with_instance(env, instanceNumber, "GroupAcceptInvite", [](TOX_ERR_GROUP_JOIN error){
        switch (error) {
            success_case(GROUP_JOIN);
            failure_case(GROUP_JOIN, FAILED);
        }

        return unhandled();
    }, [](uint32_t group_number) {
        return group_number;
    }, new_tox_group_accept_invite, inviteDataArray.data(), inviteDataArray.size());
}

int
new_tox_group_new_join(Tox *tox, const uint8_t *invite_key, TOX_ERR_GROUP_JOIN *error)
{
  int group_number = tox_group_new_join(tox, invite_key);
  if (group_number == -1) {
    *error = TOX_ERR_GROUP_JOIN_FAILED;
    return -1;
  }

  if (error) *error = TOX_ERR_GROUP_JOIN_OK;
  return group_number;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupNewJoin
 * Signature: (I[B)
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupNewJoin
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray inviteKey)
{
    ByteArray inviteKeyData(env, inviteKey);
    assert(!inviteKey || inviteKeyData.size() == TOX_GROUP_CHAT_ID_SIZE);
    return with_instance(env, instanceNumber, "GroupNewJoin", [](TOX_ERR_GROUP_JOIN error){
        switch (error) {
            success_case(GROUP_JOIN);
            failure_case(GROUP_JOIN, FAILED);
        }

        return unhandled();
    }, [](uint32_t group_number) {
        return group_number;
    }, new_tox_group_new_join, inviteKeyData.data());
}

int
new_tox_group_reconnect(Tox *tox, int groupnumber, TOX_ERR_GROUP_JOIN *error)
{
  int group_number = tox_group_reconnect(tox, groupnumber);
  if (group_number == -1) {
    *error = TOX_ERR_GROUP_JOIN_FAILED;
    return -1;
  }

  if (error) *error = TOX_ERR_GROUP_JOIN_OK;
  return group_number;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupReconnect
 * Signature: (II)
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupReconnect
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    return with_instance(env, instanceNumber, "GroupReconnect", [](TOX_ERR_GROUP_JOIN error){
        switch (error) {
            success_case(GROUP_JOIN);
            failure_case(GROUP_JOIN, FAILED);
        }

        return unhandled();
    }, [](uint32_t group_number) {
        return group_number;
    }, new_tox_group_reconnect, groupNumber);
}

int
new_tox_group_new(Tox *tox, const uint8_t *group_name, uint16_t length, TOX_ERR_GROUP_JOIN *error)
{
    if (length > TOX_MAX_NAME_LENGTH)
    {
      if (error) *error = TOX_ERR_GROUP_JOIN_FAILED; //TODO proper errors
      return false;
    }
    if (length > 0 && group_name == nullptr)
    {
      if (error) *error = TOX_ERR_GROUP_JOIN_FAILED;
      return false;
    }

  int group_number = tox_group_new(tox, group_name, length);
  if (group_number == -1) {
    *error = TOX_ERR_GROUP_JOIN_FAILED;
    return -1;
  }

  if (error) *error = TOX_ERR_GROUP_JOIN_OK;
  return group_number;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupNew
 * Signature: (I[B)
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupNew
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray groupName)
{
    ByteArray groupNameArray(env, groupName);
    return with_instance(env, instanceNumber, "GroupNew", [](TOX_ERR_GROUP_JOIN error){
        switch (error) {
            success_case(GROUP_JOIN);
            failure_case(GROUP_JOIN, FAILED);
        }

        return unhandled();
    }, [](uint32_t group_number) {
        return group_number;
    }, new_tox_group_new, groupNameArray.data(), groupNameArray.size());
}

bool
new_tox_group_delete(Tox *tox, int groupnumber, const uint8_t *partmessage, uint16_t length, TOX_ERR_GROUP_DELETE *error)
{
  int success = tox_group_delete(tox, groupnumber, partmessage, length);

  if (success == -1) {
    if (error) *error = TOX_ERR_GROUP_DELETE_FAILED;
    return false;
  } else {
    if (error) *error = TOX_ERR_GROUP_DELETE_OK;
    return true;
  }
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupDelete
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupDelete
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray partMessage)
{

    ByteArray partMessageData(env, partMessage);
    return with_instance(env, instanceNumber, "GroupDelete", [](TOX_ERR_GROUP_DELETE error) {
        switch (error) {
            success_case(GROUP_DELETE);
            failure_case(GROUP_DELETE, FAILED);
        }
        return unhandled();
    }, [](bool) {
    }, new_tox_group_delete, groupNumber, partMessageData.data(), partMessageData.size());
}

bool
new_tox_group_set_topic(Tox *tox, int groupnumber, const uint8_t *topic, uint16_t length, TOX_ERR_GROUP_SET *error) {
    int success = tox_group_set_topic(tox, groupnumber, topic, length);
    return success != -1;
}

 /* Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupSetTopic
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupSetTopic
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray topic)
{
    ByteArray topic_array(env, topic);
    return with_instance(env, instanceNumber, "GroupSetTopic", [](TOX_ERR_GROUP_SET error) {
        switch (error) {
            success_case(GROUP_SET);
            failure_case(GROUP_SET, FAILED);
        }
        return unhandled();
    }, [](bool) {
    }, new_tox_group_set_topic, groupNumber, topic_array.data(), topic_array.size());
}

bool
new_tox_group_get_topic(const Tox *tox, int groupnumber, uint8_t *topic) {
    int success = tox_group_get_topic(tox, groupnumber, topic);
    return success != -1;
}

size_t
new_tox_group_get_topic_size (Tox const *tox, int groupnumber)
{
  size_t size = tox_group_get_topic_size (tox, groupnumber);
  if (size == 1)
    {
      uint8_t topic[1];
      tox_group_get_topic (tox, groupnumber, topic);
      if (topic[0] == '\0')
        size = 0;
    }
  return size;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetTopic
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetTopic
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) -> jbyteArray {
        unused(events);
        size_t size = new_tox_group_get_topic_size (tox, groupNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> topic(size);
        new_tox_group_get_topic(tox, groupNumber, topic.data());

        return toJavaArray(env, topic);
    });
}

bool
new_tox_group_get_group_name(const Tox *tox, int groupnumber, uint8_t *groupname) {
    int success = tox_group_get_group_name(tox, groupnumber, groupname);
    return success != -1;
}

size_t
new_tox_group_get_group_name_size (Tox const *tox, int groupnumber)
{
  size_t size = tox_group_get_group_name_size (tox, groupnumber);
  if (size == 1)
    {
      uint8_t name[1];
      tox_group_get_group_name (tox, groupnumber, name);
      if (name[0] == '\0')
        size = 0;
    }
  return size;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetGroupName
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetGroupName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) -> jbyteArray {
        unused(events);
        size_t size = new_tox_group_get_group_name_size (tox, groupNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> name(size);
        new_tox_group_get_group_name(tox, groupNumber, name.data());

        return toJavaArray(env, name);
    });
}

bool
new_tox_group_get_peer_name(const Tox *tox, int groupnumber, uint32_t peernumber, uint8_t *name) {
    int success = tox_group_get_peer_name(tox, groupnumber, peernumber, name);
    return success != -1;
}

size_t
new_tox_group_get_peer_name_size (Tox const *tox, int groupnumber, uint32_t peernumber)
{
  size_t size = tox_group_get_peer_name_size (tox, groupnumber, peernumber);
  if (size == 1)
    {
      uint8_t name[1];
      tox_group_get_peer_name (tox, groupnumber, peernumber, name);
      if (name[0] == '\0')
        size = 0;
    }
  return size;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetPeerName
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetPeerName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) -> jbyteArray {
        unused(events);
        size_t size = new_tox_group_get_peer_name_size (tox, groupNumber, peerNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> name(size);
        new_tox_group_get_peer_name (tox, groupNumber, peerNumber, name.data());

        return toJavaArray(env, name);
    });
}

TOX_GROUP_STATUS
new_tox_group_get_status(const Tox *tox, int groupnumber, uint32_t peernumber) {
  return (TOX_GROUP_STATUS) tox_group_get_status(tox, groupnumber, peernumber);
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetStatus
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) {
        unused(events);
        return new_tox_group_get_status(tox, groupNumber, peerNumber);
    });
}

TOX_GROUP_ROLE
new_tox_group_get_role(const Tox *tox, int groupnumber, uint32_t peernumber) {
  return (TOX_GROUP_ROLE) tox_group_get_role(tox, groupnumber, peernumber);
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetRole
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetRole
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) {
        unused(events);
        return new_tox_group_get_role(tox, groupNumber, peerNumber);
    });
}

bool
new_tox_group_get_chat_id(const Tox *tox, int groupnumber, uint8_t *dest, TOX_ERR_GROUP_QUERY *error) {
  int success = tox_group_get_chat_id(tox, groupnumber, dest);
  if (success == -1) {
    if (error) *error = TOX_ERR_GROUP_QUERY_FAILED;
    return false;
  } else {
    if (error) *error = TOX_ERR_GROUP_QUERY_OK;
    return true;
  }
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetInviteKey
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetChatId
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    std::vector<uint8_t> buffer(TOX_GROUP_CHAT_ID_SIZE);
    return with_instance(env, instanceNumber, "GroupGetChatId", [](TOX_ERR_GROUP_QUERY error) {
        switch (error) {
            success_case(GROUP_QUERY);
            failure_case(GROUP_QUERY, FAILED);
        }
        return unhandled();
    }, [&](bool) {
        return toJavaArray(env, buffer);
    }, new_tox_group_get_chat_id, groupNumber, buffer.data());
}

int
new_tox_group_get_number_peers(const Tox *tox, int groupnumber, TOX_ERR_GROUP_QUERY *error) {
  int number_peers = tox_group_get_number_peers(tox, groupnumber);
  if (number_peers == -1) {
    if (error) *error = TOX_ERR_GROUP_QUERY_FAILED;
  } else {
    if (error) *error = TOX_ERR_GROUP_QUERY_OK;
  }
  return number_peers;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetNumberPeers
 * Signature: (II)
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetNumberPeers
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    return with_instance(env, instanceNumber, "GroupGetNumberPeers", [](TOX_ERR_GROUP_QUERY error) {
        switch (error) {
            success_case(GROUP_QUERY);
            failure_case(GROUP_QUERY, FAILED);
        }
        return unhandled();
    }, [](int number_peers) {
        return number_peers;
    }, new_tox_group_get_number_peers, groupNumber);
}

uint32_t
new_tox_group_count_groups(const Tox *tox) {
  return tox_group_count_groups(tox);
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupCountGroups
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupCountGroups
  (JNIEnv *env, jclass, jint instanceNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) {
        unused(events);
        return new_tox_group_count_groups(tox);
    });
}

