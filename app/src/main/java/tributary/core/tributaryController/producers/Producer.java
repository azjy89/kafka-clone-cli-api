package tributary.core.tributaryController.producers;

import java.util.Optional;

import org.json.JSONObject;

import tributary.api.message.Headers;
import tributary.core.tributaryController.producers.productionStrategy.ProductionStrategy;
import tributary.core.tributaryController.producers.productionStrategy.ProductionStrategyFactory;
import tributary.core.tributaryController.tributaryClusters.Topic;

public class Producer {
    private String id;
    private String type;
    private ProductionStrategy productionStrategy;
    private ProductionStrategyFactory productionStrategyFactory;

    public Producer(String id, String type, String allocation) {
        this.id = id;
        this.type = type;
        this.productionStrategyFactory = new ProductionStrategyFactory();
        this.productionStrategy = productionStrategyFactory.createProductionStrategy(allocation);
    }

    public synchronized void produceEvent(Topic topic, JSONObject eventObject, Optional<Integer> partition) {
        if (!topic.getType().equals(getType())) {
            throw new IllegalArgumentException("Producer type does not match topic type.");
        }
        JSONObject headersObject = eventObject.getJSONObject("headers");
        Headers headers = new Headers(headersObject.getString("dateTimeCreated"), headersObject.getString("ID"),
                headersObject.getString("payloadType"));
        if (!headers.getPayloadType().equals(getType())) {
            throw new IllegalArgumentException("Event type does not match producer type.");
        }
        productionStrategy.produceEvent(topic, headers, eventObject.getString("value"), partition);
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
