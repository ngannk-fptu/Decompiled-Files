/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.queue;

import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.GroupRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import java.util.List;

public interface NotificationQueueManager {
    public List<NotificationTask> getQueuedTasks();

    public void processEvent(Object var1);

    public void submitIndividualNotification(Iterable<RoleRecipient> var1, NotificationEvent var2);

    public void submitIndividualNotificationViaAddress(Iterable<NotificationAddress> var1, NotificationEvent var2);

    public void submitIndividualNotificationViaServer(Iterable<RoleRecipient> var1, NotificationEvent var2, int var3);

    public void submitGroupNotification(GroupRecipient var1, NotificationEvent var2);

    public void clear();
}

