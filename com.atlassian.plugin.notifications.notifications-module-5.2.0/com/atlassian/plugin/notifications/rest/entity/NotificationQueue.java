/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.rest.entity;

import com.atlassian.plugin.notifications.rest.entity.QueueItem;
import com.atlassian.plugin.notifications.rest.entity.ServerModel;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationQueue {
    @JsonProperty
    private final List<QueueItem> queue;
    @JsonProperty
    private final List<ServerModel> serverStatus;

    public NotificationQueue(List<QueueItem> queue, List<ServerModel> serverStatus) {
        this.queue = queue;
        this.serverStatus = serverStatus;
    }

    public List<QueueItem> getQueue() {
        return this.queue;
    }

    public List<ServerModel> getServerStatus() {
        return this.serverStatus;
    }
}

