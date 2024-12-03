/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor.poller;

import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerJobRunner;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlassianSchedulerDirectoryPollerManager
implements DirectoryPollerManager {
    private static final Logger log = LoggerFactory.getLogger(AtlassianSchedulerDirectoryPollerManager.class);
    private final SchedulerService schedulerService;

    public AtlassianSchedulerDirectoryPollerManager(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public void triggerPoll(long directoryID, SynchronisationMode syncMode) {
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)DirectoryPollerJobRunner.JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce(null)).withParameters((Map)ImmutableMap.builder().put((Object)"DIRECTORY_ID", (Object)directoryID).put((Object)"SYNC_MODE", (Object)syncMode).build());
        try {
            this.schedulerService.scheduleJobWithGeneratedId(config);
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(e);
        }
    }
}

