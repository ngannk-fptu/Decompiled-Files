/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduleType;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import com.hazelcast.util.scheduler.SecondsBasedEntryTaskScheduler;

public final class EntryTaskSchedulerFactory {
    private EntryTaskSchedulerFactory() {
    }

    public static <K, V> EntryTaskScheduler<K, V> newScheduler(TaskScheduler taskScheduler, ScheduledEntryProcessor<K, V> entryProcessor, ScheduleType scheduleType) {
        return new SecondsBasedEntryTaskScheduler<K, V>(taskScheduler, entryProcessor, scheduleType);
    }
}

