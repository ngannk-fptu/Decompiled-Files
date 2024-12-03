/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor.poller;

import com.atlassian.crowd.directory.DbCachingDirectoryPoller;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryPollerJobRunner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(DirectoryPollerJobRunner.class);
    public static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)DirectoryPollerManager.class.getName());
    public static final String PARAM_DIRECTORY_ID = "DIRECTORY_ID";
    public static final String PARAM_SYNC_MODE = "SYNC_MODE";
    private final DirectoryManager directoryManager;
    private final DirectorySynchroniser directorySynchroniser;
    private final DirectoryInstanceLoader directoryInstanceLoader;
    private final SchedulerService schedulerService;

    public DirectoryPollerJobRunner(DirectoryManager directoryManager, DirectorySynchroniser directorySynchroniser, DirectoryInstanceLoader directoryInstanceLoader, SchedulerService schedulerService) {
        this.directoryManager = directoryManager;
        this.directorySynchroniser = directorySynchroniser;
        this.directoryInstanceLoader = directoryInstanceLoader;
        this.schedulerService = schedulerService;
    }

    public void register() {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        RemoteDirectory remoteDirectory;
        Directory directory;
        Map params = request.getJobConfig().getParameters();
        long directoryId = (Long)params.get(PARAM_DIRECTORY_ID);
        try {
            directory = this.directoryManager.findDirectoryById(directoryId);
        }
        catch (DirectoryNotFoundException e) {
            log.error("Cannot synchronise unknown directory [ {} ]", (Object)directoryId, (Object)e);
            return JobRunnerResponse.aborted((String)("Cannot synchronise unknown directory [ " + directoryId + " ]"));
        }
        try {
            remoteDirectory = this.directoryInstanceLoader.getDirectory(directory);
        }
        catch (DirectoryInstantiationException e) {
            log.error("Could not instantiate directory {}", (Object)directoryId, (Object)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        if (!(remoteDirectory instanceof SynchronisableDirectory)) {
            log.warn("Directory is not synchronisable. [ {} ]", (Object)directoryId);
            return JobRunnerResponse.aborted((String)("Directory is not synchronisable. [ " + directoryId + " ]"));
        }
        SynchronisableDirectory synchronisableDirectory = (SynchronisableDirectory)remoteDirectory;
        SynchronisationMode synchronisationMode = DirectoryPollerJobRunner.getSynchronisationMode(params, synchronisableDirectory);
        DbCachingDirectoryPoller poller = new DbCachingDirectoryPoller(this.directorySynchroniser, synchronisableDirectory);
        poller.pollChanges(synchronisationMode);
        return JobRunnerResponse.success();
    }

    private static SynchronisationMode getSynchronisationMode(Map<String, Serializable> params, SynchronisableDirectory directory) {
        return (SynchronisationMode)MoreObjects.firstNonNull((Object)DirectoryPollerJobRunner.getSynchronisationModeFromJobParams(params), (Object)DirectoryPollerJobRunner.getSynchronisationModeFromDirectory(directory));
    }

    @Nullable
    private static SynchronisationMode getSynchronisationModeFromJobParams(Map<String, Serializable> params) {
        return (SynchronisationMode)params.get(PARAM_SYNC_MODE);
    }

    private static SynchronisationMode getSynchronisationModeFromDirectory(SynchronisableDirectory directory) {
        return directory.isIncrementalSyncEnabled() ? SynchronisationMode.INCREMENTAL : SynchronisationMode.FULL;
    }

    public void unregister() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }
}

