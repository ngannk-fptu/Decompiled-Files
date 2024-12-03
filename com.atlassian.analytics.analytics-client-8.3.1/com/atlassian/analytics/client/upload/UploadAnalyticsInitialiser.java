/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobRunnerKey
 */
package com.atlassian.analytics.client.upload;

import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.upload.RemoteFilterRead;
import com.atlassian.analytics.client.upload.S3EventUploader;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UploadAnalyticsInitialiser
implements LifecycleAware {
    private final SchedulerService schedulerService;
    private final PeriodicEventUploaderScheduler periodicEventUploaderScheduler;
    private final Map<JobRunnerKey, JobRunner> jobRunners = new HashMap<JobRunnerKey, JobRunner>();

    public UploadAnalyticsInitialiser(SchedulerService schedulerService, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, RemoteFilterRead remoteFilterRead, S3EventUploader s3EventUploader) {
        this.schedulerService = Objects.requireNonNull(schedulerService);
        this.periodicEventUploaderScheduler = Objects.requireNonNull(periodicEventUploaderScheduler);
        this.jobRunners.put(RemoteFilterRead.KEY, Objects.requireNonNull(remoteFilterRead));
        this.jobRunners.put(S3EventUploader.KEY, Objects.requireNonNull(s3EventUploader));
    }

    public void onStart() {
        this.jobRunners.forEach((arg_0, arg_1) -> ((SchedulerService)this.schedulerService).registerJobRunner(arg_0, arg_1));
        this.periodicEventUploaderScheduler.initialise();
    }

    public void onStop() {
        this.jobRunners.keySet().forEach(arg_0 -> ((SchedulerService)this.schedulerService).unregisterJobRunner(arg_0));
    }
}

