/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.emailtracker.api;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class EmailReadEvent {
    private final Date timestamp;
    private final ConfluenceUser recipient;
    private final ConfluenceUser actor;
    private final String action;
    private final ContentEntityObject content;
    private final Map<String, String> properties;

    public EmailReadEvent(Date timestamp, ConfluenceUser recipient, ConfluenceUser actor, String action, ContentEntityObject content, Map<String, String> properties) {
        this.timestamp = timestamp;
        this.recipient = recipient;
        this.actor = actor;
        this.action = action;
        this.content = content;
        this.properties = Collections.unmodifiableMap(properties);
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public ConfluenceUser getRecipient() {
        return this.recipient;
    }

    public ConfluenceUser getActor() {
        return this.actor;
    }

    public String getAction() {
        return this.action;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public String get(String key) {
        return this.properties.get(key);
    }
}

