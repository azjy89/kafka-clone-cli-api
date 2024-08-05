package tributary.core.tributaryController.tributaryClusters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tributary.api.message.Message;
import tributary.core.tributaryController.consumerGroups.ConsumerGroup;

public class Topic implements TopicPublisher {
    private String id;
    private String type;
    private List<Partition> partitions;
    private Map<String, ConsumerGroup> consumerGroups;

    public Topic(String id, String type) {
        this.id = id;
        this.type = type;
        this.partitions = new ArrayList<>();
        this.consumerGroups = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void addConsumerGroup(String id, ConsumerGroup consumerGroup) {
        consumerGroups.put(id, consumerGroup);
    }

    @Override
    public void notifyGroups() {
        for (ConsumerGroup consumerGroup : consumerGroups.values()) {
            consumerGroup.rebalance();
        }
    }

    public Partition getPartition(int partitionId) {
        if (partitions.stream().filter(p -> p.getKey() == partitionId).findFirst().orElse(null) == null) {
            throw new IllegalArgumentException("Partition with this Id does not exist.");
        }
        return partitions.stream().filter(p -> p.getKey() == partitionId).findFirst().orElse(null);
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public void createPartition(int partitionId) {
        synchronized (this) {
            partitions.add(new Partition(partitionId));
            notify();
        }
    }

    public void sendMessage(Message message) {
        int partitionId = message.getKey().orElse(new Random().nextInt(partitions.size()));
        partitions.get(partitionId).addMessage(message);
    }

    @Override
    public String toString() {
        String result = "Topic: " + id + "\n";
        for (Partition partition : partitions) {
            result += partition.toString();
            result += "\n";
        }
        return result;
    }
}
