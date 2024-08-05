package tributary.core.tributaryController.consumerGroups.rebalancingStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tributary.core.tributaryController.consumerGroups.Consumer;
import tributary.core.tributaryController.tributaryClusters.Partition;

public class RangeRebalancingStrategy implements RebalancingStrategy {
    @Override
    public void execute(Map<String, Consumer> consumers, List<Partition> partitions) {
        for (Consumer consumer : consumers.values()) {
            consumer.unsubscribeAll();
        }
        int numConsumers = consumers.size();

        if (consumers.isEmpty() || partitions.isEmpty()) {
            return;
        }

        int baseNum = partitions.size() / numConsumers;
        int extra = partitions.size() - baseNum * numConsumers;
        Iterator<Partition> iterator = partitions.iterator();
        for (Consumer consumer : consumers.values()) {
            for (int i = 0; i < baseNum; i++) {
                consumer.subscribe(iterator.next());
            }
            if (extra != 0) {
                consumer.subscribe(iterator.next());
                extra--;
            }
        }
    }
}
