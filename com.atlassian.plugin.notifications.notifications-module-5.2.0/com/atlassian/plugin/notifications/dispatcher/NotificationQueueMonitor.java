/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.queue.NotificationTask;

public interface NotificationQueueMonitor {
    public void taskAdded(NotificationTask var1);

    public void taskCompleted(NotificationTask var1);

    public void taskError(NotificationTask var1);
}

