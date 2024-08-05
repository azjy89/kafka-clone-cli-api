package tributary.core.tributaryController.producers.productionStrategy;

public class ProductionStrategyFactory {
    public ProductionStrategy createProductionStrategy(String type) {
        switch (type) {
        case "Manual":
            return new ManualProductionStrategy();
        case "Random":
            return new RandomProductionStrategy();
        default:
            throw new IllegalArgumentException("Unknown production strategy allocation: " + type);
        }
    }
}
