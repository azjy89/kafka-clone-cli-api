package tributary.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import tributary.api.Pair;
import tributary.api.TributaryAPI;
import tributary.api.Triple;

public class TributaryCLI {
    private TributaryAPI controller;

    public TributaryCLI() {
        this.controller = new TributaryAPI();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.startsWith("create topic")) {
                handleCreateTopic(command);
            } else if (command.startsWith("create partition")) {
                handleCreatePartition(command);
            } else if (command.startsWith("create consumergroup")) {
                handleCreateConsumerGroup(command);
            } else if (command.startsWith("create consumer")) {
                handleCreateConsumer(command);
            } else if (command.startsWith("create producer")) {
                handleCreateProducer(command);
            } else if (command.startsWith("produce event")) {
                handleProduceEvent(command);
            } else if (command.startsWith("delete consumer")) {
                handleDeleteConsumer(command);
            } else if (command.startsWith("consume event")) {
                handleConsumeEvent(command);
            } else if (command.startsWith("show topic")) {
                handleShowTopic(command);
            } else if (command.startsWith("show consumergroup")) {
                handleShowConsumerGroup(command);
            } else if (command.startsWith("playback")) {
                handlePlayback(command);
            } else if (command.startsWith("set consumer group rebalancing")) {
                handleSetReblancing(command);
            } else if (command.startsWith("parallel produce")) {
                handleParallelProduce(command);
            } else if (command.startsWith("parallel consume")) {
                handleParallelConsume(command);
            } else {
                System.out.println("Unknown command.");
            }
        }
        scanner.close();
    }

    private void handleParallelConsume(String command) {
        String[] parts = command.split(" ");
        if (parts.length % 2 != 0) {
            System.out.println("Invalid command. Usage: parallel consume <consumer> <partition> ...");
            return;
        }
        List<Pair<String, Integer>> commands = new ArrayList<>();
        for (int i = 0; i < (parts.length - 2) / 2; i++) {
            int partition;
            try {
                partition = Integer.parseInt(parts[2 * i + 3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid partition ID. It must be an integer.");
                return;
            }
            commands.add(new Pair<String, Integer>(parts[2 * i + 2], partition));
        }

        try {
            controller.parallelConsume(commands);
        } catch (Exception e) {
            System.out.println("Error parallel consuming event: " + e.getMessage());
        }
    }

    private void handleParallelProduce(String command) {
        String[] parts = command.split(" ");
        if (parts.length % 3 != 2) {
            System.out.println("Invalid command. Usage: parallel produce <producer> <topic> <event> ...");
            return;
        }
        List<Triple<String, String, String>> commands = new ArrayList<>();
        for (int i = 0; i < (parts.length - 2) / 3; i++) {
            commands.add(new Triple<String, String, String>(parts[3 * i + 2], parts[3 * i + 3], parts[3 * i + 4]));
        }

        try {
            controller.parallelProduce(commands);
        } catch (Exception e) {
            System.out.println("Error parallel producing event: " + e.getMessage());
        }
    }

    private void handleSetReblancing(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 6) {
            System.out.println("Invalid command. Usage: set consumer group rebalancing <group> <rebalancing>");
            return;
        }

        String group = parts[4];
        String rebalancing = parts[5];
        try {
            controller.setConsumerGroupRebalancing(group, rebalancing);
        } catch (Exception e) {
            System.out.println("Error rebalance setting: " + e.getMessage());
        }
    }

    private void handlePlayback(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid command. Usage: playback <consumer> <partition> <offset>");
            return;
        }

        String consumer = parts[1];
        int partition;
        try {
            partition = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid partition ID. It must be an integer.");
            return;
        }
        int offset;
        try {
            offset = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid offset. It must be an integer.");
            return;
        }

        try {
            System.out.println(controller.replayEvents(consumer, partition, offset));
        } catch (Exception e) {
            System.out.println("Error handling playback: " + e.getMessage());
        }
    }

    private void handleShowConsumerGroup(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid command. Usage: show consumergroup <group>");
            return;
        }

        String group = parts[2];

        try {
            controller.showConsumerGroup(group);
        } catch (Exception e) {
            System.out.println("Error showing group: " + e.getMessage());
        }
    }

    private void handleShowTopic(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid command. Usage: show topic <topic>");
            return;
        }

        String topic = parts[2];

        try {
            controller.showTopic(topic);
        } catch (Exception e) {
            System.out.println("Error showing topic: " + e.getMessage());
        }
    }

    private void handleConsumeEvent(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid command. Usage: consume event <consumer> <partition>");
            return;
        }

        String consumerId = parts[2];
        int partition;
        try {
            partition = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid partition ID. It must be an integer.");
            return;
        }

        try {
            System.out.println(controller.consumeEvent(consumerId, partition));
        } catch (Exception e) {
            System.out.println("Error consuming event: " + e.getMessage());
        }

    }

    private void handleCreateProducer(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 5) {
            System.out.println("Invalid command. Usage: create producer <producer> <type> <allocation>");
            return;
        }

        String producer = parts[2];
        String topic = parts[3];
        String allocation = parts[4];

        try {
            controller.createProducer(producer, topic, allocation);
        } catch (Exception e) {
            System.out.println("Error creating producer: " + e.getMessage());
        }

        System.out.println("Successfully created producer");
    }

    private void handleProduceEvent(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 5 && parts.length != 6) {
            System.out.println("Invalid command. Usage: produce event <producer> <topic> <event> <partition>");
            return;
        }

        String producer = parts[2];
        String topic = parts[3];
        String event = parts[4];
        Optional<Integer> partition = null;
        if (parts.length == 6) {
            try {
                Integer partitionInt = Integer.parseInt(parts[5]);
                partition = Optional.of(partitionInt);
            } catch (NumberFormatException e) {
                System.out.println("Invalid partition ID. It must be an integer.");
                return;
            }
        }

        try {
            controller.produceEvent(producer, topic, event, partition);
        } catch (Exception e) {
            System.out.println("Error producing event: " + e.getMessage());
        }
    }

    private void handleCreateTopic(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid command. Usage: create topic <id> <type>");
            return;
        }

        String id = parts[2];
        String type = parts[3];

        try {
            if (type.equalsIgnoreCase("Integer")) {
                controller.createTopic(id, type);
            } else if (type.equalsIgnoreCase("String")) {
                controller.createTopic(id, type);
            } else {
                System.out.println("Invalid type. Supported types: Integer, String.");
            }
        } catch (Exception e) {
            System.out.println("Error creating topic: " + e.getMessage());
        }
    }

    private void handleCreatePartition(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid command. Usage: create partition <topic> <id>");
            return;
        }

        String topicId = parts[2];
        int partitionId;
        try {
            partitionId = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid partition ID. It must be an integer.");
            return;
        }

        try {
            controller.createPartition(topicId, partitionId);
        } catch (Exception e) {
            System.out.println("Error creating partition: " + e.getMessage());
        }
    }

    private void handleCreateConsumerGroup(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 5) {
            System.out.println("Invalid command. Usage: create consumer group <id> <topic> <rebalancing>");
            return;
        }

        String id = parts[2];
        String topicId = parts[3];
        String rebalancingStrategy = parts[4];

        try {
            controller.createConsumerGroup(id, topicId, rebalancingStrategy);
        } catch (Exception e) {
            System.out.println("Error creating consumer group: " + e.getMessage());
        }
    }

    private void handleCreateConsumer(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid command. Usage: create consumer <group> <id>");
            return;
        }

        String groupId = parts[2];
        String consumerId = parts[3];

        try {
            controller.createConsumer(groupId, consumerId);
        } catch (Exception e) {
            System.out.println("Error creating consumer: " + e.getMessage());
        }
    }

    private void handleDeleteConsumer(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid command. Usage: delete consumer <group> <id>");
            return;
        }

        String consumerId = parts[2];

        try {
            controller.deleteConsumer(consumerId);
        } catch (Exception e) {
            System.out.println("Error deleting consumer: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TributaryCLI cli = new TributaryCLI();
        cli.start();
    }
}
