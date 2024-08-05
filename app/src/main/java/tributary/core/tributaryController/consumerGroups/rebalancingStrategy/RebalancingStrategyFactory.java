package tributary.core.tributaryController.consumerGroups.rebalancingStrategy;

public class RebalancingStrategyFactory {
    public static RebalancingStrategy createRebalancingStrategy(String rebalancingStrategyType) {
        switch (rebalancingStrategyType.toLowerCase()) {
        case "range":
            return new RangeRebalancingStrategy();
        case "roundrobin":
            return new RoundRobinRebalancingStrategy();
        default:
            throw new IllegalArgumentException("Invalid rebalancing strategy type: " + rebalancingStrategyType);
        }
    }
}
