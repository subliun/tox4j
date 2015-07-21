package im.tox.tox4j.core.callbacks

import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a peer changes the group topic.
 */
trait GroupTopicCallback[ToxCoreState] {
  /**
   * @param groupNumber The groupnumber of the group the topic change is intended for.
   * @param peerNumber The peernumber of the peer who changed the topic.
   * @param topic The topic data.
   */
  def groupTopic(
    groupNumber: Int, peerNumber: Int, topic: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = state
}
