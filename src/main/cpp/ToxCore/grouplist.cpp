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
