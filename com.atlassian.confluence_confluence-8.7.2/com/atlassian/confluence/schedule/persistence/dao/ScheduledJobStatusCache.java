/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.Cache
 *  com.atlassian.scheduler.config.JobId
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule.persistence.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.scheduler.config.JobId;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public interface ScheduledJobStatusCache {
    public void put(JobId var1, ScheduledJobStatus var2);

    public @Nullable ScheduledJobStatus get(JobId var1);

    public static ScheduledJobStatusCache forAtlassianCache(final Cache cache) {
        return new ScheduledJobStatusCache(){

            @Override
            public void put(JobId jobId, ScheduledJobStatus status) {
                cache.put((Object)jobId, (Object)status);
            }

            @Override
            public @Nullable ScheduledJobStatus get(JobId jobId) {
                return (ScheduledJobStatus)cache.get((Object)jobId);
            }
        };
    }
}

