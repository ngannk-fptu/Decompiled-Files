/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.AbstractErrorQueuedTaskQueue
 *  com.atlassian.core.task.TaskQueue
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusteredTaskQueue;
import com.atlassian.core.task.AbstractErrorQueuedTaskQueue;
import com.atlassian.core.task.TaskQueue;

@Deprecated(since="8.2", forRemoval=true)
public class ClusteredTaskQueueWithErrorQueue
extends AbstractErrorQueuedTaskQueue {
    public ClusteredTaskQueueWithErrorQueue(String name, ClusterManager clusterManager) {
        super((TaskQueue)new ClusteredTaskQueue(name + "-error", clusterManager), clusterManager.getFifoBuffer(name), name + "-error");
    }
}

