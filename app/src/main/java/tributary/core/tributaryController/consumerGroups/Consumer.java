package tributary.core.tributaryController.consumerGroups;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tributary.api.message.Message;
import tributary.core.tributaryController.tributaryClusters.Partition;

public class Consumer implements Publisher {
    private String id;
    private Map<Integer, Partition> partitions;
    private List<Message> consumedMessages;

    public Consumer(String id) {
        this.id = id;
        this.consumedMessages = new ArrayList<>();
        partitions = new LinkedHashMap<>();
    }

    public String getId() {
        return id;
    }

    public List<Message> getConsumedMessages() {
        return consumedMessages;
    }

    public synchronized Message consumeMessage(int partition) {
        if (notify(partition)) {
            return consumedMessages.get(consumedMessages.size() - 1);
        }
        return null;
    }

    public List<Message> playback(int partition, int offset) {
        return notifyReplay(partition, offset);
    }

    public List<Message> notifyReplay(int partition, int offset) {
        for (int partitionId : partitions.keySet()) {
            if (partition == partitionId) {
                return partitions.get(partition).replay(offset);
            }
        }

        throw new IllegalArgumentException("Partition with this Id is not assigned to this consumer");
    }

    @Override
    public void subscribe(Partition partition) {
        partitions.put(partition.getKey(), partition);
    }

    @Override
    public void unsubscribe(Integer partitionId) {
        partitions.remove(partitionId);
    }

    @Override
    public void unsubscribeAll() {
        partitions.clear();
    }

    public boolean isPartitionAssigned(int partitionId) {
        return partitions.containsKey(partitionId);
    }

    @Override
    public synchronized boolean notify(int partitionId) {
        if (partitions.get(partitionId) != null) {
            consumedMessages.add(partitions.get(partitionId).update());
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String partitionString = "";
        if (partitions == null) {
            return "consumerId='" + id + '\'' + "\npartitions:\n";
        }
        for (Map.Entry<Integer, Partition> partitionEntry : partitions.entrySet()) {
            partitionString += "Partition: " + partitionEntry.getValue().toString();
        }
        return "consumerId='" + id + '\'' + ", partitions:\n" + partitionString;
    }
}
