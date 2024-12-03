/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.impl.QueuedJob;
import com.atlassian.scheduler.config.JobId;
import java.io.Closeable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

interface SchedulerQueue
extends Closeable {
    public void add(QueuedJob var1) throws SchedulerShutdownException;

    public Map<JobId, Date> refreshClusteredJobs();

    @Nullable
    public QueuedJob remove(JobId var1);

    @Nullable
    public QueuedJob take() throws InterruptedException;

    public void pause() throws SchedulerShutdownException;

    public void resume() throws SchedulerShutdownException;

    public boolean isClosed();

    @Override
    public void close();

    public List<QueuedJob> getPendingJobs();

    public int getPendingJobsCount();

    public static class SchedulerShutdownException
    extends SchedulerServiceException {
        private static final long serialVersionUID = 38756229754957063L;

        public SchedulerShutdownException() {
            super("The scheduler has been shutdown.");
        }
    }
}

