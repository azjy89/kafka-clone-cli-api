package tributary.core.tributaryController.producers.productionStrategy;

import java.util.List;
import java.util.Optional;

import tributary.api.message.Headers;
import tributary.api.message.Message;
import tributary.core.tributaryController.tributaryClusters.Partition;
import tributary.core.tributaryController.tributaryClusters.Topic;

public class ManualProductionStrategy implements ProductionStrategy {
    @Override
    public void produceEvent(Topic topic, Headers headers, String value, Optional<Integer> partitionId) {
        List<Partition> partitions = topic.getPartitions();
        Message message = new Message(headers, partitionId, value);
        for (Partition partition : partitions) {
            if (partition.getKey() == partitionId
                    .orElseThrow(() -> new IllegalArgumentException("Optional is empty"))) {
                partition.addMessage(message);
                System.out.println("Message with messageId " + message.getHeaders().getMessageId()
                        + " produced to partition " + partitionId);
                return;
            }
        }

        throw new IllegalArgumentException("No partition found with the given key: " + partitionId);
    }

}
