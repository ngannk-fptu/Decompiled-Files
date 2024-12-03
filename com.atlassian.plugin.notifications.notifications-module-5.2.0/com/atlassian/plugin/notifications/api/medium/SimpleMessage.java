/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;

public class SimpleMessage
implements Message {
    private final String messageId;
    private final String subject;
    private final String body;
    private final UserProfile originatingUser;
    private final Map metadata;

    SimpleMessage(String messageId, String subject, String body, UserProfile originatingUser, Map metadata) {
        this.messageId = messageId;
        this.subject = subject;
        this.body = body;
        this.originatingUser = originatingUser;
        this.metadata = metadata;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    @Override
    public UserProfile getOriginatingUser() {
        return this.originatingUser;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public String getMessageId() {
        return this.messageId;
    }
}

