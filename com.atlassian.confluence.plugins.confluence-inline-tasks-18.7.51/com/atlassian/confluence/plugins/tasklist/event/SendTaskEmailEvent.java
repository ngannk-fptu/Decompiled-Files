/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.event.AbstractContentNotificationEvent;
import com.google.common.collect.ListMultimap;

public class SendTaskEmailEvent
extends AbstractContentNotificationEvent
implements NotificationEnabledEvent {
    private final ListMultimap<String, TaskModfication> tasks;

    public SendTaskEmailEvent(Object source, ContentEntityObject content, boolean suppressNotifications, ListMultimap<String, TaskModfication> tasks) {
        super(source, content, suppressNotifications);
        this.tasks = tasks;
    }

    public ListMultimap<String, TaskModfication> getTasks() {
        return this.tasks;
    }
}

