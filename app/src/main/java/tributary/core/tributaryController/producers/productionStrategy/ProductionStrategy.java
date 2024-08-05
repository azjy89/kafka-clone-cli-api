package tributary.core.tributaryController.producers.productionStrategy;

import java.util.Optional;

import tributary.api.message.Headers;
import tributary.core.tributaryController.tributaryClusters.Topic;

public interface ProductionStrategy {
    public void produceEvent(Topic topic, Headers headers, String messageId, Optional<Integer> partition);
}
