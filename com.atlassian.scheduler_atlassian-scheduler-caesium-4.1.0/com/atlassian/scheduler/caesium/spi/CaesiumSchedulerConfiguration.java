/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration
 */
package com.atlassian.scheduler.caesium.spi;

import com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration;

public interface CaesiumSchedulerConfiguration
extends SchedulerServiceConfiguration {
    public int refreshClusteredJobsIntervalInMinutes();

    public int workerThreadCount();

    public boolean useQuartzJobDataMapMigration();

    public boolean useFineGrainedSchedules();
}

