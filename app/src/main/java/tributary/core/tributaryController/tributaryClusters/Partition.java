package tributary.core.tributaryController.tributaryClusters;

import java.util.ArrayList;
import java.util.List;

import tributary.api.message.Message;

public class Partition implements Subscriber {
    private List<Message> messages;
    private int key;
    private int offset;

    public Partition(int key) {
        this.key = key;
        this.messages = new ArrayList<>();
        this.offset = 0;
    }

    public synchronized List<Message> getMessages() {
        return messages;
    }

    public int getKey() {
        return key;
    }

    public int getOffset() {
        return offset;
    }

    public void addMessage(Message message) {
        synchronized (this) {
            messages.add(message);
        }
    }

    public synchronized Message update() {
        System.out.println(offset);
        Message consumedMessage = messages.get(offset);
        offset++;
        return consumedMessage;
    }

    public List<Message> replay(int offset) {
        List<Message> replayedMessages = new ArrayList<>();
        while (offset < this.offset) {
            replayedMessages.add(messages.get(offset));
            offset++;
        }

        return replayedMessages;
    }

    public String toString() {
        String result = "\nPartition Key: " + key + ",\n" + "offset: " + offset + ",\n";
        for (Message message : messages) {
            result += message.toString();
            if (messages.iterator().hasNext()) {
                result += ",\n";
            }
        }

        return result;
    }
}
