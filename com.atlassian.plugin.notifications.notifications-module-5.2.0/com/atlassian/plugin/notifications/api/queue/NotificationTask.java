/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.api.queue;

import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.queue.RecipientDescription;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.List;

public interface NotificationTask
extends Runnable {
    public NotificationEvent getEvent();

    public RecipientType getRecipientType();

    public String getId();

    public int getSendCount();

    public TaskStatus getStatus();

    public void setState(TaskStatus.State var1);

    public void setQueuedForRetry(long var1);

    public long getNextAttemptTime();

    public List<RecipientDescription> getRecipientDescriptions(I18nResolver var1);
}

