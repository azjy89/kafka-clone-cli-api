package tributary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tributary.api.Pair;
import tributary.api.TributaryAPI;
import tributary.api.Triple;
import tributary.api.message.Message;
import tributary.core.tributaryController.TributaryController;
import tributary.core.tributaryController.consumerGroups.Consumer;
import tributary.core.tributaryController.consumerGroups.ConsumerGroup;
import tributary.core.tributaryController.producers.Producer;
import tributary.core.tributaryController.tributaryClusters.Partition;
import tributary.core.tributaryController.tributaryClusters.Topic;
import tributary.util.TestUtilHelper;

public class TributaryTest {
    private TributaryController controller;

    @BeforeEach
    public void setUp() {
        controller = new TributaryController();
        controller = TestUtilHelper.initializeController();
    }

    @Test
    public void testCreateTopic() {
        controller.createTopic("topic1", "String");
        assertDoesNotThrow(() -> controller.getTopic("topic1"));
        Topic topic = controller.getTopic("topic1");
        System.out.println(topic.toString());
    }

    @Test
    public void testCreateTopicDuplicate() {
        controller.createTopic("topic1", "String");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createTopic("topic1", "String");
        });
        assertTrue(thrown.getMessage().contains("Topic with this ID already exists."));
    }

    @Test
    public void testCreatePartition() {
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        Topic topic = controller.getTopic("topic1");
        assertDoesNotThrow(() -> topic.getPartition(1));
        System.out.println(topic.toString());
    }

    @Test
    public void testCreatePartitionInvalidTopic() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createPartition("invalidTopic", 1);
        });
        assertTrue(thrown.getMessage().contains("Topic with this ID does not exist."));
    }

    @Test
    public void testCreateConsumerGroup() {
        controller.createTopic("topic1", "String");
        controller.createConsumerGroup("group1", "topic1", "Range");
        assertDoesNotThrow(() -> controller.getConsumerGroup("group1"));
        ConsumerGroup consumerGroup = controller.getConsumerGroup("group1");
        System.out.println(consumerGroup.toString());
    }

    @Test
    public void testCreateConsumerGroupInvalidTopic() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createConsumerGroup("group1", "invalidTopic", "Range");
        });
        assertTrue(thrown.getMessage().contains("Topic with this ID does not exist."));
    }

    @Test
    public void testCreateConsumer() {
        controller.createTopic("topic1", "String");
        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        ConsumerGroup group = controller.getConsumerGroup("group1");
        assertDoesNotThrow(() -> group.getConsumer("consumer1"));
        System.out.println(group.toString());
    }

    @Test
    public void testCreateConsumerInvalidGroup() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createConsumer("invalidGroup", "consumer1");
        });
        assertTrue(thrown.getMessage().contains("Consumer group with this ID does not exist."));
    }

    @Test
    public void testDeleteConsumer() {
        controller.createTopic("topic1", "String");
        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        controller.deleteConsumer("consumer1");
        ConsumerGroup group = controller.getConsumerGroup("group1");
        assertThrows(IllegalArgumentException.class, () -> group.getConsumer("consumer1"));
    }

    @Test
    public void testDeleteConsumerInvalidGroup() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.deleteConsumer("consumer1");
        });
        assertTrue(thrown.getMessage().contains("Consumer with this ID does not exist."));
    }

    @Test
    public void testCreateProducer() {
        controller.createProducer("producer1", "String", "Random");
        assertDoesNotThrow(() -> controller.getProducer("producer1"));
        Producer producer = controller.getProducer("producer1");
        System.out.println(producer.getType() + " " + producer.getId());
    }

    @Test
    public void testCreateProducerDuplicate() {
        controller.createProducer("producer1", "String", "Random");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createProducer("producer1", "String", "Random");
        });
        assertTrue(thrown.getMessage().contains("Producer with this ID already exists."));
    }

    @Test
    public void testCreateProducerInvalidStrategy() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createProducer("producer1", "String", "InvalidStrategy");
        });
        assertTrue(thrown.getMessage().contains("Unknown production strategy allocation: InvalidStrategy"));
    }

    @Test
    public void testAPI() {
        TributaryAPI tributaryAPI = new TributaryAPI();
        tributaryAPI.createProducer("producer1", "String", "Manual");
        tributaryAPI.createTopic("topic1", "String");
        tributaryAPI.createPartition("topic1", 1);
        tributaryAPI.createPartition("topic1", 2);
        tributaryAPI.createPartition("topic1", 3);

        // Producing events
        tributaryAPI.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        tributaryAPI.produceEvent("producer1", "topic1", "basicEvent2", Optional.of(Integer.valueOf(2)));
        tributaryAPI.produceEvent("producer1", "topic1", "basicEvent3", Optional.of(Integer.valueOf(3)));

        tributaryAPI.createConsumerGroup("group1", "topic1", "Range");
        tributaryAPI.createConsumer("group1", "consumer1");
        tributaryAPI.createConsumer("group1", "consumer2");

        assertDoesNotThrow(() -> tributaryAPI.showTopic("topic1"));
        assertDoesNotThrow(() -> tributaryAPI.showConsumerGroup("group1"));
    }

    @Test
    public void testRandomProduceEvent() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.produceEvent("producer1", "topic1", "basicEvent", null);

        Topic topic = controller.getTopic("topic1");
        Partition partition = topic.getPartition(1);
        assertTrue(partition.getMessages().size() == 1);

        Message message = partition.getMessages().get(0);
        System.out.println(message.toString());

        String expectedEventValue = "First Basic Event Value";
        assertTrue(message.toString().contains(expectedEventValue));
    }

    @Test
    public void testRandomProduceEventNoPartition() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("producer1", "topic1", "basicEvent", null);
        });
        assertTrue(thrown.getMessage().contains("No partitions available in the topic."));
    }

    @Test
    public void testRandomProduceEventInvalidProducer() {
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("invalidProducer", "topic1", "basicEvent", null);
        });
        assertTrue(thrown.getMessage().contains("Producer with this Id does not exist"));
    }

    @Test
    public void testRandomProduceEventInvalidTopic() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("producer1", "invalidTopic", "basicEvent", null);
        });
        assertTrue(thrown.getMessage().contains("Topic with this ID does not exist."));
    }

    @Test
    public void testManualProduceEvent() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));

        Topic topic = controller.getTopic("topic1");
        Partition partition = topic.getPartition(1);
        assertTrue(partition.getMessages().size() == 1);

        Message message = partition.getMessages().get(0);
        System.out.println(message.toString());

        String expectedEventValue = "First Basic Event Value";
        assertTrue(message.toString().contains(expectedEventValue));
    }

    @Test
    public void testManualProduceEventInvalidPartition() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        });
        assertTrue(thrown.getMessage().contains("No partition found with the given key:"));
    }

    @Test
    public void testMultipleManualProduce() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);
        controller.createPartition("topic1", 3);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        controller.produceEvent("producer1", "topic1", "basicEvent2", Optional.of(Integer.valueOf(2)));
        controller.produceEvent("producer1", "topic1", "basicEvent3", Optional.of(Integer.valueOf(3)));

        Topic topic = controller.getTopic("topic1");

        Partition partition1 = topic.getPartition(1);
        Partition partition2 = topic.getPartition(2);
        Partition partition3 = topic.getPartition(3);

        assertTrue(partition1.getMessages().size() == 1);
        assertTrue(partition2.getMessages().size() == 1);
        assertTrue(partition3.getMessages().size() == 1);

        Message message1 = partition1.getMessages().get(0);
        System.out.println(message1.toString());
        String expectedEventValue1 = "First Basic Event Value";
        assertTrue(message1.toString().contains(expectedEventValue1));

        Message message2 = partition2.getMessages().get(0);
        System.out.println(message2.toString());
        String expectedEventValue2 = "Second Basic Event Value";
        assertTrue(message2.toString().contains(expectedEventValue2));

        Message message3 = partition3.getMessages().get(0);
        System.out.println(message3.toString());
        String expectedEventValue3 = "Third Basic Event Value";
        assertTrue(message3.toString().contains(expectedEventValue3));
    }

    @Test
    public void testMultipleRandomProduce() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", null);
        controller.produceEvent("producer1", "topic1", "basicEvent2", null);
        controller.produceEvent("producer1", "topic1", "basicEvent3", null);

        Topic topic = controller.getTopic("topic1");

        Partition partition1 = topic.getPartition(1);
        Partition partition2 = topic.getPartition(2);

        int totalMessages = 0;
        totalMessages += partition1.getMessages().size();
        totalMessages += partition2.getMessages().size();

        assertTrue(totalMessages == 3);

        // Collect messages from both partitions
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(partition1.getMessages());
        allMessages.addAll(partition2.getMessages());

        // Verify the contents of each message
        String expectedEventValue1 = "First Basic Event Value";
        String expectedEventValue2 = "Second Basic Event Value";
        String expectedEventValue3 = "Third Basic Event Value";

        boolean containsEvent1 = false;
        boolean containsEvent2 = false;
        boolean containsEvent3 = false;

        for (Message message : allMessages) {
            String messageContent = message.toString();
            if (messageContent.contains(expectedEventValue1)) {
                containsEvent1 = true;
            } else if (messageContent.contains(expectedEventValue2)) {
                containsEvent2 = true;
            } else if (messageContent.contains(expectedEventValue3)) {
                containsEvent3 = true;
            }
        }

        assertTrue(containsEvent1);
        assertTrue(containsEvent2);
        assertTrue(containsEvent3);

        System.out.println(partition1.getMessages());
        System.out.println(partition2.getMessages());
    }

    @Test
    public void testParallelProduce() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);

        // Preparing to produce events in parallel
        List<Triple<String, String, String>> producersAndEvents = new ArrayList<>();
        producersAndEvents.add(new Triple<>("producer1", "topic1", "basicEvent"));
        producersAndEvents.add(new Triple<>("producer1", "topic1", "basicEvent2"));
        producersAndEvents.add(new Triple<>("producer1", "topic1", "basicEvent3"));

        // Producing events in parallel
        controller.parallelProduce(producersAndEvents);

        // Allow some time for threads to complete execution
        try {
            Thread.sleep(400); // Adjust the time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Topic topic = controller.getTopic("topic1");

        Partition partition1 = topic.getPartition(1);
        Partition partition2 = topic.getPartition(2);

        int totalMessages = 0;
        totalMessages += partition1.getMessages().size();
        totalMessages += partition2.getMessages().size();

        assertTrue(totalMessages == 3);

        // Collect messages from both partitions
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(partition1.getMessages());
        allMessages.addAll(partition2.getMessages());

        // Verify the contents of each message
        String expectedEventValue1 = "First Basic Event Value";
        String expectedEventValue2 = "Second Basic Event Value";
        String expectedEventValue3 = "Third Basic Event Value";

        boolean containsEvent1 = false;
        boolean containsEvent2 = false;
        boolean containsEvent3 = false;

        for (Message message : allMessages) {
            String messageContent = message.toString();
            if (messageContent.contains(expectedEventValue1)) {
                containsEvent1 = true;
            } else if (messageContent.contains(expectedEventValue2)) {
                containsEvent2 = true;
            } else if (messageContent.contains(expectedEventValue3)) {
                containsEvent3 = true;
            }
        }

        assertTrue(containsEvent1);
        assertTrue(containsEvent2);
        assertTrue(containsEvent3);

        System.out.println(partition1.getMessages());
        System.out.println(partition2.getMessages());
    }

    @Test
    public void testMultipleRandomProduceNoPartition() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");

        // Attempt to produce events without creating partitions
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("producer1", "topic1", "basicEvent", null);
        });
        assertTrue(thrown.getMessage().contains("No partitions available in the topic."));
    }

    @Test
    public void consumeEvent() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));

        Topic topic = controller.getTopic("topic1");
        Partition partition = topic.getPartition(1);
        assertTrue(partition.getMessages().size() == 1);
        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        Message message = controller.consumeEvent("consumer1", 1);
        Consumer consumer = controller.getConsumer("consumer1");
        assertTrue(consumer.getConsumedMessages().contains(message));
        System.out.println(consumer.toString());

        String basicEventValue = "First Basic Event Value";
        assertTrue(consumer.toString().contains(basicEventValue));
    }

    @Test
    public void testConsumeEventInvalidConsumer() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.consumeEvent("invalidConsumer", 1);
        });
        assertTrue(thrown.getMessage().contains("Consumer with this ID does not exist."));
    }

    @Test
    public void parallelConsumeEvent() {
        controller.createProducer("producer1", "String", "Random");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", null);
        controller.produceEvent("producer1", "topic1", "basicEvent2", null);
        controller.produceEvent("producer1", "topic1", "basicEvent3", null);

        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumerGroup("group2", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        controller.createConsumer("group2", "consumer2");

        List<Pair<String, Integer>> pairs = new ArrayList<>();
        pairs.add(new Pair<String, Integer>("consumer1", 1));
        pairs.add(new Pair<String, Integer>("consumer2", 1));

        // Consuming events in parallel
        controller.parallelConsume(pairs);

        Consumer consumer1 = controller.getConsumer("consumer1");
        Consumer consumer2 = controller.getConsumer("consumer2");

        String consumer1Messages = consumer1.getConsumedMessages().toString();
        String consumer2Messages = consumer2.getConsumedMessages().toString();

        // Check that consumer1 and consumer2 have a message that are not the same
        assertTrue(consumer1Messages.contains("First Basic Event Value")
                || consumer1Messages.contains("Second Basic Event Value"));
        assertTrue(consumer2Messages.contains("First Basic Event Value")
                || consumer2Messages.contains("Second Basic Event Value"));
        assertNotEquals(consumer1Messages, consumer2Messages);

        System.out.println(consumer1.getConsumedMessages());
        System.out.println("\n\n");
        System.out.println(consumer2.getConsumedMessages());
    }

    @Test
    public void testSetConsumerGroupRebalancingInvalidGroup() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> controller.setConsumerGroupRebalancing("invalidGroup", "Range"));
        assertEquals("Consumer group with this ID does not exist.", thrown.getMessage());
    }

    @Test
    public void testSetConsumerGroupRebalancingInvalidStrategy() {
        controller.createTopic("topic1", "String");
        controller.createConsumerGroup("group1", "topic1", "Range");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.setConsumerGroupRebalancing("group1", "InvalidStrategy");
        });
        assertTrue(thrown.getMessage().contains("Invalid rebalancing strategy type:"));
    }

    @Test
    public void testCreateConsumerGroupDuplicate() {
        controller.createTopic("topic1", "String");
        controller.createConsumerGroup("group1", "topic1", "Range");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createConsumerGroup("group1", "topic1", "Range");
        });
        assertTrue(thrown.getMessage().contains("Consumer group with this ID already exists."));
    }

    @Test
    public void testConsumeEventInvalidPartition() {
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createProducer("producer1", "String", "Manual");
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(1));

        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.consumeEvent("consumer1", 2);
        });
        assertTrue(thrown.getMessage()
                .contains("Partition with this ID does not exist or is not assigned to this consumer."));
    }

    @Test
    public void testProduceEventInvalidPartitionManual() {
        controller.createTopic("topic1", "String");
        controller.createProducer("producer1", "String", "Manual");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(1));
        });
        assertTrue(thrown.getMessage().contains("No partition found with the given key:"));
    }

    @Test
    public void testProduceEventInvalidStrategy() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.createProducer("producer1", "String", "InvalidStrategy");
        });
        assertTrue(thrown.getMessage().contains("Unknown production strategy allocation: InvalidStrategy"));
    }

    @Test
    public void testParallelConsumeInvalidConsumer() {
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createProducer("producer1", "String", "Manual");
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(1));

        List<Pair<String, Integer>> consumersAndPartitions = List.of(new Pair<>("invalidConsumer", 1));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.parallelConsume(consumersAndPartitions);
        });
        assertTrue(thrown.getMessage().contains("Consumer with this ID does not exist."));
    }

    @Test
    public void testParallelConsumeInvalidPartition() {
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createProducer("producer1", "String", "Manual");
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(1));

        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");

        List<Pair<String, Integer>> consumersAndPartitions = List.of(new Pair<>("consumer1", 2));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            controller.parallelConsume(consumersAndPartitions);
        });
        assertTrue(thrown.getMessage()
                .contains("Partition with this ID does not exist or is not assigned to this consumer."));
    }

    @Test
    public void roundRobinRebalancing() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);
        controller.createPartition("topic1", 3);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        controller.produceEvent("producer1", "topic1", "basicEvent2", Optional.of(Integer.valueOf(2)));
        controller.produceEvent("producer1", "topic1", "basicEvent3", Optional.of(Integer.valueOf(3)));

        controller.createConsumerGroup("group1", "topic1", "RoundRobin");
        controller.createConsumer("group1", "consumer1");
        controller.createConsumer("group1", "consumer2");

        Consumer consumer1 = controller.getConsumer("consumer1");
        Consumer consumer2 = controller.getConsumer("consumer2");

        System.out.println(consumer1.toString());
        System.out.println(consumer2.toString());

        // check that consumers are assigned to the right partitions based on the round robin strat
        assertTrue(consumer1.isPartitionAssigned(1));
        assertTrue(consumer2.isPartitionAssigned(2));
        assertTrue(consumer1.isPartitionAssigned(3));

    }

    @Test
    public void testIllegalArgExceptions() {
        IllegalArgumentException topicException = assertThrows(IllegalArgumentException.class, () -> {
            controller.getTopic("Skibidi");
        });
        assertTrue(topicException.getMessage().contains("Topic with this ID does not exist."));

        IllegalArgumentException producerException = assertThrows(IllegalArgumentException.class, () -> {
            controller.getProducer("Skibidi");
        });
        assertTrue(producerException.getMessage().contains("Producer with this Id does not exist."));

        IllegalArgumentException nonExistentConsumerGroupException = assertThrows(IllegalArgumentException.class,
                () -> {
                    controller.getConsumerGroup("Skibidi");
                });
        assertTrue(nonExistentConsumerGroupException.getMessage().contains("No consumer group with this Id exists."));

        IllegalArgumentException nonExistentTopicId = assertThrows(IllegalArgumentException.class, () -> {
            controller.createConsumerGroup("Skibidi", "Skibidi", "Skibidi");
        });
        assertTrue(nonExistentTopicId.getMessage().contains("Topic with this ID does not exist."));

    }

    @Test
    public void rangeRebalancing() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);
        controller.createPartition("topic1", 3);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        controller.produceEvent("producer1", "topic1", "basicEvent2", Optional.of(Integer.valueOf(2)));
        controller.produceEvent("producer1", "topic1", "basicEvent3", Optional.of(Integer.valueOf(3)));

        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        controller.createConsumer("group1", "consumer2");

        Consumer consumer1 = controller.getConsumer("consumer1");
        Consumer consumer2 = controller.getConsumer("consumer2");

        System.out.println(consumer1.toString());
        System.out.println(consumer2.toString());

        // check that consumers are assigned to the right partitions based on the range strat
        assertTrue(consumer1.isPartitionAssigned(1));
        assertTrue(consumer1.isPartitionAssigned(2));
        assertTrue(consumer2.isPartitionAssigned(3));

        controller.createConsumer("group1", "consumer3");
        Consumer consumer3 = controller.getConsumer("consumer3");

        controller.setConsumerGroupRebalancing("group1", "RoundRobin");
        assertTrue(consumer1.isPartitionAssigned(1));
        assertTrue(consumer2.isPartitionAssigned(2));
        assertTrue(consumer3.isPartitionAssigned(3));
    }

    @Test
    public void testShow() {
        controller.createProducer("producer1", "String", "Manual");
        controller.createTopic("topic1", "String");
        controller.createPartition("topic1", 1);
        controller.createPartition("topic1", 2);
        controller.createPartition("topic1", 3);

        // Producing events
        controller.produceEvent("producer1", "topic1", "basicEvent", Optional.of(Integer.valueOf(1)));
        controller.produceEvent("producer1", "topic1", "basicEvent2", Optional.of(Integer.valueOf(2)));
        controller.produceEvent("producer1", "topic1", "basicEvent3", Optional.of(Integer.valueOf(3)));

        controller.createConsumerGroup("group1", "topic1", "Range");
        controller.createConsumer("group1", "consumer1");
        controller.createConsumer("group1", "consumer2");

        assertDoesNotThrow(() -> controller.showTopic("topic1"));
        assertDoesNotThrow(() -> controller.showConsumerGroup("group1"));
    }

}
