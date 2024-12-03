/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.message.MessageCollection
 */
package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.atlassian.sal.core.message.DefaultMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultMessageCollection
implements MessageCollection {
    private final List<Message> messages = new ArrayList<Message>();

    public void addMessage(String key, Serializable ... arguments) {
        this.addMessage(new DefaultMessage(key, arguments));
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public void addAll(List<Message> remoteMessages) {
        this.messages.addAll(remoteMessages);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Message message : this.messages) {
            builder.append(message);
            builder.append("\n");
        }
        return builder.toString();
    }
}

