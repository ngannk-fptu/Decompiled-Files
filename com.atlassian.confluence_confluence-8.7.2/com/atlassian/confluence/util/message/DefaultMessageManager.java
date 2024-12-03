/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.Message;
import com.atlassian.confluence.util.message.MessageManager;
import java.util.ArrayList;
import java.util.List;

public class DefaultMessageManager
implements MessageManager {
    private final List<MessageManager> managers;

    public DefaultMessageManager(List<MessageManager> managers) {
        this.managers = managers;
    }

    @Override
    public void addMessage(Message message) {
        throw new UnsupportedOperationException("Add your message to the specific MessageManager implementation");
    }

    @Override
    public List<Message> getMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (MessageManager manager : this.managers) {
            List<Message> list = manager.getMessages();
            if (list == null) continue;
            messages.addAll(list);
        }
        return messages;
    }

    @Override
    public void removeMessage(String id) {
        for (MessageManager manager : this.managers) {
            manager.removeMessage(id);
        }
    }
}

