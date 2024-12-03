/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import com.hazelcast.util.scheduler.ScheduledEntry;

public interface EntryTaskScheduler<K, V> {
    public boolean schedule(long var1, K var3, V var4);

    public ScheduledEntry<K, V> cancel(K var1);

    public int cancelIfExists(K var1, V var2);

    public ScheduledEntry<K, V> get(K var1);

    public void cancelAll();
}

