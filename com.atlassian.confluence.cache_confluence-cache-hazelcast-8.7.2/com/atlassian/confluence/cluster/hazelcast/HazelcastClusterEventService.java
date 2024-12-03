/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.cluster.event.ClusterEventService
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.hazelcast.core.IExecutorService
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.impl.cluster.event.ClusterEventService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.hazelcast.core.IExecutorService;
import java.util.concurrent.Executor;

@Deprecated(forRemoval=true)
public interface HazelcastClusterEventService {
    public IExecutorService getExecutorService();

    public void publishEventToCluster(Object var1);

    public void start();

    public void stop();

    default public ClusterEventService asClusterEventService() {
        return event -> Futures.submit(() -> this.publishEventToCluster(event), (Executor)MoreExecutors.directExecutor());
    }
}

