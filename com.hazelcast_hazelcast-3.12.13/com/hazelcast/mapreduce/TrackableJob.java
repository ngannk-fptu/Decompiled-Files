/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.mapreduce.JobTracker;

@Deprecated
public interface TrackableJob<V> {
    public JobTracker getJobTracker();

    public String getName();

    public String getJobId();

    public ICompletableFuture<V> getCompletableFuture();

    public JobProcessInformation getJobProcessInformation();
}

