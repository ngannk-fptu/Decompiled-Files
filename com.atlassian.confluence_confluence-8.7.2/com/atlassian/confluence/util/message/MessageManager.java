/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.Message;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MessageManager {
    public void addMessage(Message var1);

    @Transactional(readOnly=true)
    public List<Message> getMessages();

    public void removeMessage(String var1);
}

