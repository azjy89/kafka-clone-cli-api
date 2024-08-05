package tributary.core.tributaryController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import tributary.api.Pair;
import tributary.api.Triple;
import tributary.api.message.Message;
import tributary.core.tributaryController.consumerGroups.Consumer;
import tributary.core.tributaryController.consumerGroups.ConsumerGroup;
import tributary.core.tributaryController.producers.Producer;
import tributary.core.tributaryController.tributaryClusters.Topic;
import tributary.core.tributaryController.tributaryClusters.TributaryCluster;
import tributary.util.EventLoader;

public class TributaryController {
    private Map<String, ConsumerGroup> consumerGroups;
    private Map<String, Producer> producers;

    public TributaryController() {
        this.consumerGroups = new HashMap<>();
        this.producers = new HashMap<>();
    }

    public void createTopic(String id, String type) {
        TributaryCluster.getInstance().createTopic(id, type);
    }

    public Topic getTopic(String id) {
        if (TributaryCluster.getInstance().getTopic(id) == null) {
            throw new IllegalArgumentException("Topic with this ID does not exist.");
        }
        return TributaryCluster.getInstance().getTopic(id);
    }

    public Producer getProducer(String producerId) {
        if (producers.get(producerId) == null) {
            throw new IllegalArgumentException("Producer with this Id does not exist.");
        }
        return producers.get(producerId);
    }

    public void createPartition(String topicId, int partitionId) {
        TributaryCluster.getInstance().createPartition(topicId, partitionId);
    }

    public void createConsumerGroup(String id, String topicId, String rebalancingStrategyType) {
        if (consumerGroups.containsKey(id)) {
            throw new IllegalArgumentException("Consumer group with this ID already exists.");
        }
        Topic topic = TributaryCluster.getInstance().getTopic(topicId);
        if (topic == null) {
            throw new IllegalArgumentException("Topic with this ID does not exist.");
        }

        ConsumerGroup consumerGroup = new ConsumerGroup(id, topic, rebalancingStrategyType);
        consumerGroups.put(id, consumerGroup);
        System.out.println("Successfully created consumer group.");
    }

    public ConsumerGroup getConsumerGroup(String id) {
        if (consumerGroups.get(id) == null) {
            throw new IllegalArgumentException("No consumer group with this Id exists.");
        }
        return consumerGroups.get(id);
    }

    public void createConsumer(String groupId, String consumerId) {
        ConsumerGroup consumerGroup = consumerGroups.get(groupId);
        if (consumerGroup == null) {
            throw new IllegalArgumentException("Consumer group with this ID does not exist.");
        }

        consumerGroup.addConsumer(new Consumer(consumerId));
        System.out.println("Consumer created: ID = " + consumerId + " in consumer group " + groupId);
    }

    public void deleteConsumer(String consumerId) {
        for (ConsumerGroup consumerGroup : consumerGroups.values()) {
            if (consumerGroup.getConsumer(consumerId) != null) {
                consumerGroup.removeConsumer(consumerId);
                System.out.println(
                        "Consumer deleted: ID = " + consumerId + " from consumer group " + consumerGroup.getId());
                return;
            }
        }

        throw new IllegalArgumentException("Consumer with this ID does not exist.");
    }

    // Method to create a producer
    public void createProducer(String id, String type, String allocation) {
        if (producers.containsKey(id)) {
            throw new IllegalArgumentException("Producer with this ID already exists.");
        }
        producers.put(id, new Producer(id, type, allocation));
    }

    // Method to produce an event
    public synchronized void produceEvent(String producer, String topicId, String event, Optional<Integer> partition) {
        JSONObject eventObject;
        try {
            eventObject = new JSONObject(EventLoader.loadEventFile(event));
        } catch (IOException e) {
            e.printStackTrace();
            eventObject = null;
        }
        if (producers.get(producer) == null) {
            throw new IllegalArgumentException("Producer with this Id does not exist");
        }
        producers.get(producer).produceEvent(TributaryCluster.getInstance().getTopic(topicId), eventObject, partition);
    }

    // Method to consume an event
    public synchronized Message consumeEvent(String consumerId, int partition) {
        Consumer consumer = null;
        for (ConsumerGroup consumerGroup : consumerGroups.values()) {
            if (consumerGroup.containsConsumer(consumerId)) {
                consumer = consumerGroup.getConsumer(consumerId);
            }
        }
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer with this ID does not exist.");
        }
        if (!consumer.isPartitionAssigned(partition)) {
            throw new IllegalArgumentException(
                    "Partition with this ID does not exist or is not assigned to this consumer.");
        }
        return consumer.consumeMessage(partition);
    }

    // Method to consume multiple events
    public void consumeEvents(String consumer, int partition, int numberOfEvents) {
        for (int i = 0; i < numberOfEvents; i++) {
            System.out.println(consumeEvent(consumer, partition));
        }
    }

    // Method to show details of a topic
    public void showTopic(String topicId) {
        System.out.println(TributaryCluster.getInstance().topicToString(topicId));
    }

    // Method to show details of a consumer group
    public void showConsumerGroup(String groupId) {
        System.out.println(consumerGroups.get(groupId).toString());
    }

    // Method for parallel producing of events
    public synchronized void parallelProduce(List<Triple<String, String, String>> producersAndEvents) {
        for (Triple<String, String, String> triple : producersAndEvents) {
            String producerId = triple.getFirst();
            String topicId = triple.getSecond();
            String event = triple.getThird();
            Thread thread = new Thread(() -> produceEvent(producerId, topicId, event, null));
            thread.start();
        }
    }

    // Method for parallel consuming of events
    public synchronized void parallelConsume(List<Pair<String, Integer>> consumersAndPartitions) {
        for (Pair<String, Integer> pair : consumersAndPartitions) {
            String consumerId = pair.getFirst();
            Integer partitionId = pair.getSecond();
            Thread thread = new Thread(() -> System.out.println(consumeEvent(consumerId, partitionId)));
            thread.run();
        }
    }

    // Method to set consumer group rebalancing
    public void setConsumerGroupRebalancing(String group, String rebalancingStrategy) {
        ConsumerGroup consumerGroup = consumerGroups.get(group);
        if (consumerGroup == null) {
            throw new IllegalArgumentException("Consumer group with this ID does not exist.");
        }
        consumerGroup.setRebalancingStrategy(rebalancingStrategy);
        System.out.println("Rebalancing strategy successfully set");
    }

    // Method for playback of events
    public List<Message> playback(String consumerId, int partition, int offset) {
        Consumer consumer = getConsumer(consumerId);
        return consumer.playback(partition, offset);
    }

    public Consumer getConsumer(String consumerId) {
        for (ConsumerGroup consumerGroup : consumerGroups.values()) {
            if (consumerGroup.containsConsumer(consumerId)) {
                return consumerGroup.getConsumer(consumerId);
            }
        }

        throw new IllegalArgumentException("Consumer with this Id does not exist.");
    }

    public void clear() {
        if (consumerGroups != null) {
            consumerGroups.clear();
        }
        if (producers != null) {
            producers.clear();
        }
    }

}
