/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.ErrorQueuedTaskQueue
 *  com.atlassian.core.task.TaskQueueWithErrorQueue
 */
package com.atlassian.confluence.event.queues;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusteredTaskQueueWithErrorQueue;
import com.atlassian.core.task.ErrorQueuedTaskQueue;
import com.atlassian.core.task.TaskQueueWithErrorQueue;

public class TaskQueueWithErrorQueueFactory {
    private final ClusterManager clusterManager;

    public TaskQueueWithErrorQueueFactory() {
        this.clusterManager = null;
    }

    @Deprecated(since="8.2", forRemoval=true)
    public TaskQueueWithErrorQueueFactory(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Deprecated
    public TaskQueueWithErrorQueue getClusteredQueue(String name) {
        if (this.clusterManager != null && this.clusterManager.isClustered()) {
            return new ClusteredTaskQueueWithErrorQueue(name, this.clusterManager);
        }
        return this.getLocalQueue(name);
    }

    public TaskQueueWithErrorQueue getLocalQueue(String name) {
        return new ErrorQueuedTaskQueue();
    }
}

