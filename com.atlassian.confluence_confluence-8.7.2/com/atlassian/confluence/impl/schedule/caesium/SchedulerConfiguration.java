/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration
 *  com.google.common.base.Supplier
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration;
import com.google.common.base.Supplier;
import java.util.TimeZone;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SchedulerConfiguration
implements CaesiumSchedulerConfiguration {
    private static final int REFRESH_INTERVAL_IN_MINUTES = 5;
    private static final int WORKER_THREAD_COUNT = 4;
    private final ClusterManager clusterManager;
    private final Supplier<TimeZoneManager> timeZoneManager;

    public SchedulerConfiguration(ClusterManager clusterManager, Supplier<TimeZoneManager> timeZoneManager) {
        this.clusterManager = clusterManager;
        this.timeZoneManager = timeZoneManager;
    }

    public int refreshClusteredJobsIntervalInMinutes() {
        return this.clusterManager.isClustered() ? 5 : 0;
    }

    public int workerThreadCount() {
        return 4;
    }

    public boolean useQuartzJobDataMapMigration() {
        return false;
    }

    public boolean useFineGrainedSchedules() {
        return true;
    }

    public @Nullable TimeZone getDefaultTimeZone() {
        return ((TimeZoneManager)this.timeZoneManager.get()).getDefaultTimeZone();
    }
}

