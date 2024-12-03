/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.Message;
import com.atlassian.confluence.util.message.MessageManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessageManager
implements MessageManager {
    @Override
    public List<Message> getMessages() {
        Map<String, Message> map = this.retrieveEntries();
        return map == null ? null : new ArrayList<Message>(map.values());
    }

    @Override
    public void removeMessage(String id) {
        Map<String, Message> entries = this.retrieveEntries();
        if (entries != null) {
            entries.remove(id);
            this.saveEntries(entries);
        }
    }

    @Override
    public void addMessage(Message message) {
        Map<String, Message> entries = this.retrieveEntries();
        if (entries == null) {
            entries = new HashMap<String, Message>();
        }
        entries.put(message.getId(), message);
        this.saveEntries(entries);
    }

    protected abstract Map<String, Message> retrieveEntries();

    protected abstract void saveEntries(Map<String, Message> var1);
}

