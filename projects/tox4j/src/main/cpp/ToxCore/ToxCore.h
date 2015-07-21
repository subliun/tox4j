// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// Protobuf classes.
#include "Core.pb.h"

// JNI declarations from javah.
#include "im_tox_tox4j_impl_jni_ToxCoreJni.h"

// Header from toxcore.
#include <tox/core.h>

#ifndef SUBSYSTEM
#define SUBSYSTEM TOX
#define CLASS     ToxCore
#define PREFIX    tox
#endif

namespace core
{
  namespace proto = im::tox::tox4j::core::proto;

  using Events = proto::CoreEvents;

  extern ToxInstances<tox::core_ptr, std::unique_ptr<Events>> instances;
}




template<typename T, size_t get_size (Tox const *), void get_data (Tox const *, T *)>
typename java_array_t<T>::array_type
get_vector (JNIEnv *env, Tox const *tox)
{
  std::vector<T> name (get_size (tox));
  get_data (tox, name.data ());

  return toJavaArray (env, name);
}


template<typename T, size_t size, void get_data (Tox const *, T *)>
typename java_array_t<T>::array_type
get_array (JNIEnv *env, Tox const *tox)
{
  std::vector<T> name (size);
  get_data (tox, name.data ());

  return toJavaArray (env, name);
}

template<typename T, typename Error, Error OK, typename ...Args>
struct get_vector_err
{
  template<size_t get_size (Tox const *, Args..., Error *), bool get_data (Tox const *, Args..., T *,  Error *)>
  static std::vector<T>
  compose (Tox const *tox, Args ...args, Error *error)
  {
    std::size_t size = get_size (tox, args..., error);
    if (*error != OK)
      return { };

    std::vector<T> vector (size);
    get_data (tox, args..., vector.data (), error);

    return vector;
  }
};
