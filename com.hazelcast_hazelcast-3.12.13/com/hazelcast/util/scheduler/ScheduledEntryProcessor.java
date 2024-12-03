/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduledEntry;
import java.util.Collection;

public interface ScheduledEntryProcessor<K, V> {
    public void process(EntryTaskScheduler<K, V> var1, Collection<ScheduledEntry<K, V>> var2);
}

