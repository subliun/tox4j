#include "ToxAv.h"

using AvInstanceManager = instance_manager<Subsystem>;
using AvInstance = tox_instance<Subsystem>;

static void
tox4j_call_cb (ToxAV *av, uint32_t friend_number, bool audio_enabled, bool video_enabled, void *user_data)
{
  unused (av);
  Events &events = *static_cast<Events *> (user_data);
  auto msg = events.add_call ();
  msg->set_friendnumber (friend_number);
  msg->set_audioenabled (audio_enabled);
  msg->set_videoenabled (video_enabled);
}

static void
tox4j_call_state_cb (ToxAV *av, uint32_t friend_number, uint32_t state, void *user_data)
{
  unused (av);
  Events &events = *static_cast<Events *> (user_data);
  auto msg = events.add_callstate ();
  msg->set_friendnumber (friend_number);
  msg->set_state (state);
}

static void
tox4j_receive_audio_frame_cb (ToxAV *av,
                              uint32_t friend_number,
                              int16_t const *pcm,
                              size_t sample_count,
                              uint8_t channels,
                              uint32_t sampling_rate,
                              void *user_data)
{
  unused (av);
  Events &events = *static_cast<Events *> (user_data);
  auto msg = events.add_receiveaudioframe ();
  msg->set_friendnumber (friend_number);

  for (size_t i = 0; i < sample_count; i++)
    msg->add_pcm (pcm[i]);

  msg->set_channels (channels);
  msg->set_samplingrate (sampling_rate);
}

static void
tox4j_receive_video_frame_cb (ToxAV *av,
                              uint32_t friend_number,
                              uint16_t width, uint16_t height,
                              uint8_t const *y, uint8_t const *u, uint8_t const *v,
                              int32_t ystride, int32_t ustride, int32_t vstride,
                              void *user_data)
{
  unused (av);
  Events &events = *static_cast<Events *> (user_data);
  auto msg = events.add_receivevideoframe ();

  int y_length = width * height;
  int u_length = (width / 2) * (height / 2);
  int v_length = (width / 2) * (height / 2);

  std::vector<uint8_t> y_vec(y_length);
  std::vector<uint8_t> u_vec(u_length);
  std::vector<uint8_t> v_vec(v_length);

  msg->set_friendnumber (friend_number);
  msg->set_width (width);
  msg->set_height (height);
  msg->set_y (y_vec.data(), y_length);
  msg->set_u (u_vec.data(), u_length);
  msg->set_v (v_vec.data(), v_length);
}


/*
 * Class:     im_tox_tox4jToxAvImpl
 * Method:    toxAvNew
 * Signature: (ZZILjava/lang/String;I)I
 */
TOX_METHOD (jint, New, jint toxInstanceNumber)
{
  return with_instance (env, toxInstanceNumber, [=] (Tox *tox, tox_traits<Tox>::events &) {
    TOXAV_ERR_NEW error;
    AvInstance::pointer av (toxav_new (tox, &error));

    auto events = std::unique_ptr<Events> (new Events);

    // Set up our callbacks.
    toxav_callback_call                (av.get (), tox4j_call_cb,                events.get ());
    toxav_callback_call_state          (av.get (), tox4j_call_state_cb,          events.get ());
    toxav_callback_receive_audio_frame (av.get (), tox4j_receive_audio_frame_cb, events.get ());
    toxav_callback_receive_video_frame (av.get (), tox4j_receive_video_frame_cb, events.get ());

    AvInstance instance {
      std::move (av),
      std::move (events),
      std::unique_ptr<std::mutex> (new std::mutex)
    };

    return AvInstanceManager::self.add (std::move (instance));
  });
}

/*
 * Class:     im_tox_tox4jToxAvImpl
 * Method:    toxAvKill
 * Signature: (I)I
 */
TOX_METHOD (void, Kill,
  jint instanceNumber)
{
  AvInstanceManager::self.kill (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4jToxAvImpl
 * Method:    finalize
 * Signature: (I)V
 */
METHOD (void, finize,
  jint instanceNumber)
{
  AvInstanceManager::self.finalize (env, instanceNumber);
}
