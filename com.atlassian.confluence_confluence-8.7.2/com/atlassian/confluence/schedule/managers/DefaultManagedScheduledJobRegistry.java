/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.managers.ManagedScheduledJobRegistry;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultManagedScheduledJobRegistry
implements ManagedScheduledJobRegistry {
    private static final Logger log = LoggerFactory.getLogger(DefaultManagedScheduledJobRegistry.class);
    private final SchedulerService schedulerService;
    private final Map<JobId, ManagedScheduledJob> standardJobs;
    private final Map<JobId, ManagedScheduledJob> pluginJobs = new HashMap<JobId, ManagedScheduledJob>();

    public DefaultManagedScheduledJobRegistry(ClusterManager clusterManager, SchedulerService schedulerService, Collection<ManagedScheduledJob> standardJobs) {
        this.schedulerService = schedulerService;
        HashMap<JobId, ManagedScheduledJob> mutableStandardJobs = new HashMap<JobId, ManagedScheduledJob>();
        for (ManagedScheduledJob job : standardJobs) {
            if (job.isClusteredOnly() && !clusterManager.isClustered()) continue;
            this.registerAtlassianJobRunner(job);
            mutableStandardJobs.put(job.getJobId(), job);
        }
        this.standardJobs = Collections.unmodifiableMap(mutableStandardJobs);
    }

    @Override
    public synchronized Collection<ManagedScheduledJob> getManagedScheduledJobs() {
        ArrayList<ManagedScheduledJob> jobs = new ArrayList<ManagedScheduledJob>(this.standardJobs.size() + this.pluginJobs.size());
        jobs.addAll(this.standardJobs.values());
        jobs.addAll(this.pluginJobs.values());
        return Collections.unmodifiableCollection(jobs);
    }

    @Override
    public synchronized ManagedScheduledJob getManagedScheduledJob(JobId jobId) {
        return this.standardJobs.getOrDefault(jobId, this.pluginJobs.get(jobId));
    }

    @Override
    public synchronized boolean isManaged(JobId jobId) {
        return this.standardJobs.containsKey(jobId) || this.pluginJobs.containsKey(jobId);
    }

    @Override
    public synchronized void addManagedScheduledJob(ManagedScheduledJob job) {
        if (job.getJobId() != null) {
            this.registerAtlassianJobRunner(job);
            this.pluginJobs.put(job.getJobId(), job);
        } else {
            log.error("Unable to manage job with null jobId: {}", (Object)job);
        }
    }

    @Override
    public synchronized void removeManagedScheduledJob(ManagedScheduledJob job) {
        this.pluginJobs.remove(job.getJobId());
        this.unregisterAtlassianJobRunner(job);
    }

    private void registerAtlassianJobRunner(ManagedScheduledJob job) {
        this.schedulerService.registerJobRunner(job.getJobConfig().getJobRunnerKey(), job.getJobRunner());
    }

    private void unregisterAtlassianJobRunner(ManagedScheduledJob job) {
        this.schedulerService.unregisterJobRunner(job.getJobConfig().getJobRunnerKey());
    }
}

