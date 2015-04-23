#include "ToxCore.h"


/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetPublicKey
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetPublicKey,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [env] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_array<uint8_t, TOX_PUBLIC_KEY_SIZE,
          tox_self_get_public_key> (env, tox);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetSecretKey
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetSecretKey,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [env] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_array<uint8_t, TOX_SECRET_KEY_SIZE,
          tox_self_get_secret_key> (env, tox);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfSetNospam
 * Signature: (II)V
 */
TOX_METHOD (void, SelfSetNospam,
  jint instanceNumber, jint nospam)
{
  return with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        unused (events);
        tox_self_set_nospam (tox, nospam);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetNospam
 * Signature: (I)I
 */
TOX_METHOD (jint, SelfGetNospam,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [=] (Tox const *tox, Events &events)
      {
        unused (events);
        return tox_self_get_nospam (tox);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetAddress
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetAddress,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [env] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_array<uint8_t, TOX_ADDRESS_SIZE,
          tox_self_get_address> (env, tox);
      }
  );
}


static ErrorHandling
handle_set_info_error (TOX_ERR_SET_INFO error)
{
  switch (error)
    {
    success_case (SET_INFO);
    failure_case (SET_INFO, NULL);
    failure_case (SET_INFO, TOO_LONG);
    }
  return unhandled ();
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfSetName
 * Signature: (I[B)V
 */
TOX_METHOD (void, SelfSetName,
  jint instanceNumber, jbyteArray name)
{
  ByteArray name_array (env, name);
  return with_instance (env, instanceNumber, "SetInfo",
    handle_set_info_error, 
    tox_self_set_name, name_array.data (), name_array.size ());
}

bool
new_tox_group_set_self_name(Tox *tox, int groupnumber, const uint8_t *name, uint16_t length, TOX_ERR_GROUP_SET_NAME *error) {
    int success = tox_group_set_self_name(tox, groupnumber, name, length);
    if (success == -1) {
      if (error) *error = TOX_ERR_GROUP_SET_NAME_FAILED;
      return false;
    } else if (success == -2) {
      if (error) *error = TOX_ERR_GROUP_SET_NAME_TAKEN;
      return false;
    } else {
      if (error) *error = TOX_ERR_GROUP_SET_NAME_OK;
      return true;
    }
}

static ErrorHandling handle_group_set_self_name_error(TOX_ERR_GROUP_SET_NAME error) {
    switch (error) {
        success_case(GROUP_SET_NAME);
        failure_case(GROUP_SET_NAME, FAILED);
        failure_case(GROUP_SET_NAME, TAKEN);
    }
    return unhandled();
}

 /* Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupSetSelfName
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupSetSelfName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray name)
{
    ByteArray name_array(env, name);
    return with_instance(env, instanceNumber, "GroupSetSelfName", handle_group_set_self_name_error, [](bool) {
    }, new_tox_group_set_self_name, groupNumber, name_array.data(), name_array.size());
}

bool
new_tox_group_get_self_name(const Tox *tox, int groupnumber, uint8_t *name)
{
    int success = tox_group_get_self_name(tox, groupnumber, name);
    return success != -1;
}

size_t
new_tox_group_get_self_name_size (Tox const *tox, int groupnumber)
{
  size_t size = tox_group_get_self_name_size (tox, groupnumber);
  if (size == 1)
    {
      uint8_t name[1];

      tox_group_get_self_name (tox, groupnumber, name);
      if (name[0] == '\0')
        size = 0;
    }
  return size;
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxGroupGetSelfName
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_ToxCoreImpl_toxGroupGetSelfName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
    return with_instance(env, instanceNumber, [=](Tox *tox, Events &events) -> jbyteArray {
        unused(events);
        size_t size = new_tox_group_get_self_name_size(tox, groupNumber);
        if (size == 0) {
            return nullptr;
        }
        std::vector<uint8_t> name(size);
        new_tox_group_get_self_name(tox, groupNumber, name.data());

        return toJavaArray(env, name);
    });
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetName
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetName,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [env] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_vector<uint8_t,
          tox_self_get_name_size,
          tox_self_get_name> (env, tox);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfSetStatusMessage
 * Signature: (I[B)V
 */
TOX_METHOD (void, SelfSetStatusMessage,
  jint instanceNumber, jbyteArray statusMessage)
{
  ByteArray status_message_array (env, statusMessage);
  return with_instance (env, instanceNumber, "SetInfo",
    handle_set_info_error,
    tox_self_set_status_message, status_message_array.data (), status_message_array.size ());
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetStatusMessage
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetStatusMessage,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [env] (Tox const *tox, Events &events)
      {
        unused (events);
        return get_vector<uint8_t,
          tox_self_get_status_message_size,
          tox_self_get_status_message> (env, tox);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfSetStatus
 * Signature: (II)V
 */
TOX_METHOD (void, SelfSetStatus,
  jint instanceNumber, jint status)
{
  return with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        unused (events);
        TOX_USER_STATUS const status_enum = [=] {
          switch (status)
            {
            case 0: return TOX_USER_STATUS_NONE;
            case 1: return TOX_USER_STATUS_AWAY;
            case 2: return TOX_USER_STATUS_BUSY;
            }
          fatal ("Invalid user status from Java");
        } ();
        tox_self_set_status (tox, status_enum);
      }
  );
}

/*
 * Class:     im_tox_tox4jToxCoreImpl
 * Method:    toxSelfGetStatus
 * Signature: (I)I
 */
TOX_METHOD (jint, SelfGetStatus,
  jint instanceNumber)
{
  return with_instance (env, instanceNumber,
    [=] (Tox const *tox, Events &events)
      {
        unused (events);
        switch (tox_self_get_status (tox))
          {
          case TOX_USER_STATUS_NONE: return 0;
          case TOX_USER_STATUS_AWAY: return 1;
          case TOX_USER_STATUS_BUSY: return 2;
          }
        fatal ("Invalid result from tox_self_get_status");
      }
  );
}
