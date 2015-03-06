#include "ToxCore.h"

static ErrorHandling
handle_send_error(TOX_ERR_GROUP_SEND error)
{
    switch (error) {
        success_case(GROUP_SEND);
        failure_case(GROUP_SEND, FAILED);
    }
    return unhandled();
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupMessageSend
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupMessageSend
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray message)
{
    ByteArray message_array(env, message);
    return with_instance(env, instanceNumber, "GroupMessageSend", handle_send_error, [](bool) {
    }, tox_group_message_send, groupNumber, message_array.data(), message_array.size());
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupPrivateMessageSend
 * Signature: (II[BI)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupPrivateMessageSend
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerNumber, jbyteArray message)
{
    ByteArray message_array(env, message);
    return with_instance(env, instanceNumber, "GroupPrivateMessageSend", handle_send_error, [](bool) {
    }, tox_group_private_message_send, groupNumber, peerNumber, message_array.data(), message_array.size());
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupActionSend
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupActionSend
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray message)
{
    ByteArray message_array(env, message);
    return with_instance(env, instanceNumber, "GroupActionSend", handle_send_error, [](bool) {
    }, tox_group_action_send, groupNumber, message_array.data(), message_array.size());
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupSetStatus
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupSetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint status)
{
    return with_instance(env, instanceNumber, "GroupSetStatus", [](TOX_ERR_GROUP_SET error) {
        switch (error) {
            success_case(GROUP_SET);
            failure_case(GROUP_SET, FAILED);
        }
        return unhandled();
    }, [](bool) {
    }, tox_group_set_status, groupNumber, status);
}