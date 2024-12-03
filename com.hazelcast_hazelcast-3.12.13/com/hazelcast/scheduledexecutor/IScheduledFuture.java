/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import java.util.concurrent.ScheduledFuture;

public interface IScheduledFuture<V>
extends ScheduledFuture<V> {
    public ScheduledTaskHandler getHandler();

    public ScheduledTaskStatistics getStats();

    public void dispose();

    @Override
    public boolean cancel(boolean var1);
}

