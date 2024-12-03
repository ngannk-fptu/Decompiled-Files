/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.message;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.List;

public interface MessageCollection {
    public void addMessage(String var1, Serializable ... var2);

    public void addMessage(Message var1);

    public void addAll(List<Message> var1);

    public boolean isEmpty();

    public List<Message> getMessages();
}

