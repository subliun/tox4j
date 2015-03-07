#include "ToxCore.h"

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
    }, tox_group_new_join, inviteKeyData.data());
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
    }, tox_group_delete, groupNumber, partMessageData.data(), partMessageData.size());
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
    }, tox_group_set_topic, groupNumber, topic_array.data(), topic_array.size());
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
        size_t size = tox_group_get_topic_size (tox, groupNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> topic(size);
        tox_group_get_topic(tox, groupNumber, topic.data());

        return toJavaArray(env, topic);
    });
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
        size_t size = tox_group_get_group_name_size (tox, groupNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> name(size);
        tox_group_get_group_name(tox, groupNumber, name.data());

        return toJavaArray(env, name);
    });
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
        size_t size = tox_group_get_peer_name_size (tox, groupNumber, peerNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> name(size);
        tox_group_get_peer_name (tox, groupNumber, peerNumber, name.data());

        return toJavaArray(env, name);
    });
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
        return tox_group_get_status(tox, groupNumber, peerNumber);
    });
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
        return tox_group_get_role(tox, groupNumber, peerNumber);
    });
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
    }, tox_group_get_chat_id, groupNumber, buffer.data());
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
    }, tox_group_get_number_peers, groupNumber);
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
        return tox_group_count_groups(tox);
    });
}
