/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.TrackableJob;

@Deprecated
public interface JobTracker
extends DistributedObject {
    public <K, V> Job<K, V> newJob(KeyValueSource<K, V> var1);

    public <V> TrackableJob<V> getTrackableJob(String var1);
}

