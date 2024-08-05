package tributary.core.tributaryController.consumerGroups.rebalancingStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tributary.core.tributaryController.consumerGroups.Consumer;
import tributary.core.tributaryController.tributaryClusters.Partition;

public class RoundRobinRebalancingStrategy implements RebalancingStrategy {
    @Override
    public void execute(Map<String, Consumer> consumers, List<Partition> partitions) {
        for (Consumer consumer : consumers.values()) {
            consumer.unsubscribeAll();
        }

        Iterator<Consumer> iterator = consumers.values().iterator();
        for (Partition partition : partitions) {
            if (!iterator.hasNext()) {
                iterator = consumers.values().iterator();
            }
            Consumer consumer = iterator.next();
            System.out.println(consumer.getId());
            consumer.subscribe(partition);
        }
    }
}
