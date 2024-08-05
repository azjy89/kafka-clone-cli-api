package tributary.core.tributaryController.tributaryClusters;

import java.util.HashMap;
import java.util.Map;

public class TributaryCluster {
    private Map<String, Topic> topics;

    private static TributaryCluster instance;

    private TributaryCluster() {
        this.topics = new HashMap<>();
    }

    public static TributaryCluster getInstance() {
        if (instance == null) {
            instance = new TributaryCluster();
        }

        return instance;
    }

    public void createTopic(String id, String type) {
        if (topics.containsKey(id)) {
            throw new IllegalArgumentException("Topic with this ID already exists.");
        }
        Topic topic = new Topic(id, type);
        topics.put(id, topic);
        System.out.println("Topic created: ID = " + id + ", Type = " + type);
    }

    public Topic getTopic(String id) {
        Topic topic = topics.get(id);
        if (topic == null) {
            throw new IllegalArgumentException("Topic with this ID does not exist.");
        }
        return topic;
    }

    public void createPartition(String topicId, int partitionId) {
        Topic topic = topics.get(topicId);
        if (topic == null) {
            throw new IllegalArgumentException("Topic with this ID does not exist.");
        }
        topic.createPartition(partitionId);
        System.out.println("Partition created: ID = " + partitionId + " in topic " + topicId);
    }

    public String topicToString(String id) {
        return topics.get(id).toString();
    }

    public void clear() {
        if (topics != null) {
            topics.clear();
        }
    }
}
