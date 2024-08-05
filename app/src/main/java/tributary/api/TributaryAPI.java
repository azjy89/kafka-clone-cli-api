package tributary.api;

import java.util.List;
import java.util.Optional;

import tributary.api.message.Message;
import tributary.core.tributaryController.TributaryController;

public class TributaryAPI {
    private TributaryController controller;

    /**
     * Constructor to initialize the TributaryController.
     */
    public TributaryAPI() {
        this.controller = new TributaryController();
    }

    /**
     * Creates a new topic in the system.
     *
     * @param id   The identifier for the topic.
     * @param type The type of events that the topic will handle.
     */
    public void createTopic(String id, String type) {
        controller.createTopic(id, type);
    }

    /**
     * Creates a new partition within a specified topic.
     *
     * @param topicId     The identifier for the topic.
     * @param partitionId The identifier for the partition.
     */
    public void createPartition(String topicId, int partitionId) {
        controller.createPartition(topicId, partitionId);
    }

    /**
     * Creates a new producer in the system.
     *
     * @param id       The identifier for the producer.
     * @param type     The type of events that the producer will handle.
     * @param strategy The strategy for assigning messages to partitions (Random or Manual).
     */
    public void createProducer(String id, String type, String strategy) {
        controller.createProducer(id, type, strategy);
    }

    /**
     * Produces a new event from a producer to a specified topic.
     *
     * @param producerId The identifier for the producer.
     * @param topicId    The identifier for the topic.
     * @param event      A JSON object representing the event.
     * @param partition  An optional parameter specifying the partition.
     */
    public void produceEvent(String producerId, String topicId, String event, Optional<Integer> partition) {
        controller.produceEvent(producerId, topicId, event, partition);
    }

    /**
     * Creates a new consumer group in the system.
     *
     * @param groupId   The identifier for the consumer group.
     * @param topicId   The identifier for the topic.
     * @param strategy  The rebalancing strategy for the consumer group (Range or RoundRobin).
     */
    public void createConsumerGroup(String groupId, String topicId, String strategy) {
        controller.createConsumerGroup(groupId, topicId, strategy);
    }

    /**
     * Creates a new consumer within a specified consumer group.
     *
     * @param groupId   The identifier for the consumer group.
     * @param consumerId The identifier for the consumer.
     */
    public void createConsumer(String groupId, String consumerId) {
        controller.createConsumer(groupId, consumerId);
    }

    /**
     * Deletes a consumer with a specified consumer id.
     *
     * @param consumerId The identifier for the consumer.
     */
    public void deleteConsumer(String consumerId) {
        controller.deleteConsumer(consumerId);
    }

    /**
     * Consumes an event from a specified partition by a specified consumer.
     *
     * @param consumerId  The identifier for the consumer.
     * @param partitionId The identifier for the partition.
     */
    public Message consumeEvent(String consumerId, int partitionId) {
        return controller.consumeEvent(consumerId, partitionId);
    }

    /**
     * Replays events for a given consumer from a specified offset.
     *
     * @param consumerId  The identifier for the consumer.
     * @param partitionId The identifier for the partition.
     * @param offset      The offset from which to start replaying events.
     * @return A list of messages that were replayed.
     */
    public List<Message> replayEvents(String consumerId, int partitionId, int offset) {
        return controller.playback(consumerId, partitionId, offset);
    }

    /**
     * Sets the rebalancing strategy for a specified consumer group.
     *
     * @param groupId          The identifier for the consumer group.
     * @param rebalancingStrategy The new rebalancing strategy (Range or RoundRobin).
     */
    public void setConsumerGroupRebalancing(String groupId, String rebalancingStrategy) {
        controller.setConsumerGroupRebalancing(groupId, rebalancingStrategy);
    }

    /**
     * Shows the details of a specified topic.
     *
     * @param topicId The identifier for the topic.
     */
    public void showTopic(String topicId) {
        controller.showTopic(topicId);
    }

    /**
     * Shows the details of a specified consumer group.
     *
     * @param groupId The identifier for the consumer group.
     */
    public void showConsumerGroup(String groupId) {
        controller.showConsumerGroup(groupId);
    }

    /**
     * Parallelly produces multiple events.
     *
     * @param producersAndEvents A list of triples containing producerId, topicId, and event details.
     */
    public void parallelProduce(List<Triple<String, String, String>> producersAndEvents) {
        controller.parallelProduce(producersAndEvents);
    }

    /**
     * Consumes multiple events in parallel.
     *
     * @param consumersAndPartitions A list of pairs containing consumerId and partitionId.
     */
    public void parallelConsume(List<Pair<String, Integer>> consumersAndPartitions) {
        controller.parallelConsume(consumersAndPartitions);
    }
}
