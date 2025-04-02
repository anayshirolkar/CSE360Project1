package HW2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Messages {
    private List<Message> messages;

    public Messages() {
        this.messages = new ArrayList<>();
    }

    // Create
    public void sendMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        messages.add(message);
    }

    // Read
    public List<Message> getMessagesForRecipient(String recipient) {
        return messages.stream()
            .filter(m -> m.getRecipient().equals(recipient))
            .collect(Collectors.toList());
    }

    // Delete
    public void deleteMessage(String messageId) {
        messages.removeIf(m -> m.getId().equals(messageId));
    }
}