/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.SimpleMessage;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;

public class SimpleMessageBuilder {
    private String messageId;
    private String subject;
    private String body;
    private UserProfile originatingUser;
    private Map metadata;

    public SimpleMessageBuilder(SimpleMessage original) {
        this.messageId = original.getMessageId();
        this.subject = original.getSubject();
        this.body = original.getBody();
        this.originatingUser = original.getOriginatingUser();
        this.metadata = original.getMetadata();
    }

    public SimpleMessageBuilder(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public SimpleMessageBuilder messageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public SimpleMessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public SimpleMessageBuilder body(String body) {
        this.body = body;
        return this;
    }

    public SimpleMessageBuilder originatingUser(UserProfile originatingUser) {
        this.originatingUser = originatingUser;
        return this;
    }

    public SimpleMessageBuilder metadata(Map metadata) {
        this.metadata = metadata;
        return this;
    }

    public SimpleMessage build() {
        return new SimpleMessage(this.messageId, this.subject, this.body, this.originatingUser, this.metadata);
    }

    public static SimpleMessageBuilder create(String subject, String body) {
        return new SimpleMessageBuilder(subject, body);
    }

    public static SimpleMessageBuilder create(SimpleMessage original) {
        return new SimpleMessageBuilder(original);
    }
}

