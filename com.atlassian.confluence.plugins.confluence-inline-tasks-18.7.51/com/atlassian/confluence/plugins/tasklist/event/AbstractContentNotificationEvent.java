/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.ContentEvent
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;

public abstract class AbstractContentNotificationEvent
extends ContentEvent {
    private ContentEntityObject content;

    @Deprecated
    public AbstractContentNotificationEvent(Object source, ContentEntityObject content) {
        super(source, false);
        this.content = content;
    }

    public AbstractContentNotificationEvent(Object source, ContentEntityObject content, boolean suppressNotifications) {
        super(source, suppressNotifications);
        this.content = content;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }
}

