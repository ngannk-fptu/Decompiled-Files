/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.AbstractTaskQueue
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.core.task.AbstractTaskQueue;

@Deprecated(since="8.2", forRemoval=true)
public class ClusteredTaskQueue
extends AbstractTaskQueue {
    public ClusteredTaskQueue(String name, ClusterManager clusterManager) {
        super(clusterManager.getFifoBuffer(name), name);
    }

    public String getName() {
        return super.getQueueName();
    }
}

