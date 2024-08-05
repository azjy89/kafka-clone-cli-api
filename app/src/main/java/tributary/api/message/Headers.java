package tributary.api.message;

public class Headers {
    private String dateTimeCreated;
    private String messageId;
    private String payloadType;

    // Constructor, Getters, and Setters
    public Headers(String dateTimeCreated, String messageId, String payloadType) {
        this.dateTimeCreated = dateTimeCreated;
        this.messageId = messageId;
        this.payloadType = payloadType;
    }

    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getPayloadType() {
        return payloadType;
    }

    @Override
    public String toString() {
        return "\n\t\"datetimeCreated\":\"" + dateTimeCreated + "\",\n\t" + "\"messageId\":\"" + messageId + "\",\n\t"
                + "\"payloadType\":\"" + payloadType + "\"";
    }
}
