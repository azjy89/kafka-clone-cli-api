package tributary.core.tributaryController.consumerGroups.rebalancingStrategy;

import java.util.List;
import java.util.Map;

import tributary.core.tributaryController.consumerGroups.Consumer;
import tributary.core.tributaryController.tributaryClusters.Partition;

public interface RebalancingStrategy {
    void execute(Map<String, Consumer> consumers, List<Partition> partitions);
}
