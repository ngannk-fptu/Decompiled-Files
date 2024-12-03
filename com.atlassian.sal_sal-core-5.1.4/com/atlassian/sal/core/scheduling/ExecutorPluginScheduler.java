/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 */
package com.atlassian.sal.core.scheduling;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorPluginScheduler
implements PluginScheduler {
    public static final int DEFAULT_POOL_SIZE = 5;
    private final ScheduledExecutorService jobExecutor;
    private final Map<String, Future<?>> jobs;

    public ExecutorPluginScheduler() {
        this(Executors.newScheduledThreadPool(5));
    }

    public ExecutorPluginScheduler(ScheduledExecutorService executor) {
        this.jobExecutor = executor;
        this.jobs = new ConcurrentHashMap();
    }

    public synchronized void scheduleJob(String jobKey, Class<? extends PluginJob> jobClass, Map<String, Object> jobDataMap, Date startTime, long repeatInterval) {
        Future<?> job = this.jobs.get(jobKey);
        if (job != null) {
            this.cancelJob(job);
        }
        this.jobs.put(jobKey, this.jobExecutor.scheduleAtFixedRate(new Job(jobClass, jobDataMap), this.getDelay(startTime), repeatInterval, TimeUnit.MILLISECONDS));
    }

    public synchronized void unscheduleJob(String jobKey) {
        Future<?> job = this.jobs.remove(jobKey);
        if (job == null) {
            throw new IllegalArgumentException("Attempted to unschedule unknown job: " + jobKey);
        }
        this.cancelJob(job);
    }

    protected void cancelJob(Future<?> job) {
        job.cancel(false);
    }

    private long getDelay(Date startTime) {
        long time = startTime.getTime() - System.currentTimeMillis();
        return time > 0L ? time : 0L;
    }

    private static class Job
    implements Runnable {
        private final Class<? extends PluginJob> jobClass;
        private final Map<String, Object> jobDataMap;

        private Job(Class<? extends PluginJob> jobClass, Map<String, Object> jobDataMap) {
            this.jobClass = jobClass;
            this.jobDataMap = jobDataMap;
        }

        @Override
        public void run() {
            PluginJob job;
            try {
                job = this.jobClass.newInstance();
            }
            catch (InstantiationException ie) {
                throw new IllegalStateException("Error instantiating job", ie);
            }
            catch (IllegalAccessException iae) {
                throw new IllegalStateException("Cannot access job class", iae);
            }
            job.execute(this.jobDataMap);
        }
    }
}

