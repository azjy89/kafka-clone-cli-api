package tributary.core.tributaryController.consumerGroups;

import java.util.LinkedHashMap;
import java.util.Map;

import tributary.core.tributaryController.consumerGroups.rebalancingStrategy.RebalancingStrategy;
import tributary.core.tributaryController.consumerGroups.rebalancingStrategy.RebalancingStrategyFactory;
import tributary.core.tributaryController.tributaryClusters.Topic;

public class ConsumerGroup {
    private String id;
    private Topic topic;
    private RebalancingStrategy rebalancingStrategy;
    private Map<String, Consumer> consumers;

    public ConsumerGroup(String id, Topic topic, String rebalancingStrategyType) {
        this.id = id;
        this.topic = topic;
        this.rebalancingStrategy = RebalancingStrategyFactory.createRebalancingStrategy(rebalancingStrategyType);
        this.consumers = new LinkedHashMap<>();
        topic.addConsumerGroup(id, this);
    }

    public String getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public RebalancingStrategy getRebalancingStrategy() {
        return rebalancingStrategy;
    }

    public void setRebalancingStrategy(String rebalancingStrategy) {
        this.rebalancingStrategy = RebalancingStrategyFactory.createRebalancingStrategy(rebalancingStrategy);
        rebalance();
    }

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

    public Consumer getConsumer(String consumerId) {
        if (consumers.get(consumerId) == null) {
            throw new IllegalArgumentException("No Consumer with this Id exists.");
        }
        return consumers.get(consumerId);
    }

    public boolean containsConsumer(String consumerId) {
        if (consumers.get(consumerId) == null) {
            return false;
        }
        return true;
    }

    public void addConsumer(Consumer consumer) {
        consumers.put(consumer.getId(), consumer);
        rebalance();
    }

    public void removeConsumer(String consumerId) {
        consumers.remove(consumerId);
        rebalance();
    }

    public void rebalance() {
        rebalancingStrategy.execute(consumers, topic.getPartitions());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Consumer Group: ").append(id).append("\n");
        for (Consumer consumer : consumers.values()) {
            sb.append("Consumer: ").append(consumer.toString()).append("\n\n");
        }
        return sb.toString();
    }
}
