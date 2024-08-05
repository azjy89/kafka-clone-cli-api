package tributary.core.tributaryController.producers.productionStrategy;

import java.util.List;
import java.util.Optional;

import tributary.api.message.Headers;
import tributary.api.message.Message;
import tributary.core.tributaryController.tributaryClusters.Partition;
import tributary.core.tributaryController.tributaryClusters.Topic;

public class RandomProductionStrategy implements ProductionStrategy {
    @Override
    public synchronized void produceEvent(Topic topic, Headers headers, String value, Optional<Integer> partitionId) {
        List<Partition> partitions = topic.getPartitions();
        Message message = new Message(headers, partitionId, value);
        if (partitions.isEmpty()) {
            throw new IllegalArgumentException("No partitions available in the topic.");
        }
        Partition minPartition = partitions.get(0);
        for (Partition partition : partitions) {
            if (minPartition.getMessages().size() > partition.getMessages().size()) {
                minPartition = partition;
            }
        }
        System.out.println("Message with eventId " + message.getHeaders().getMessageId() + " produced to partition "
                + minPartition.getKey());
        minPartition.addMessage(message);
    }

}
