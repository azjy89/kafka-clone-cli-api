package tributary.api.message;

import java.util.Optional;

public class Message {
    private Headers headers;
    private Optional<Integer> key;
    private String value; // Generic type

    // Constructor, Getters, and Setters
    public Message(Headers headers, Optional<Integer> partition, String value) {
        this.headers = headers;
        this.key = partition;
        this.value = value;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public Optional<Integer> getKey() {
        return key;
    }

    public void setKey(Optional<Integer> key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return headers.getPayloadType();
    }

    @Override
    public String toString() {
        return "\"headers\":" + headers.toString() + ",\n" + "\"key\":" + key + ",\n" + "\"value\":\"" + value;
    }
}
