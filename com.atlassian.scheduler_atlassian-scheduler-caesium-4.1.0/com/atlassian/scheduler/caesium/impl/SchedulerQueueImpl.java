/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.GuardedBy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.caesium.impl.QueuedJob;
import com.atlassian.scheduler.caesium.impl.SchedulerQueue;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.JobId;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SchedulerQueueImpl
implements SchedulerQueue {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerQueueImpl.class);
    private static final long DEFAULT_PAUSED_CHECK_TIME_MS = 60000L;
    private static final int QUEUE_SIZE_HINT = 256;
    @GuardedBy(value="lock")
    private final PriorityQueue<QueuedJob> queue = new PriorityQueue(256);
    @GuardedBy(value="lock")
    private final Map<JobId, QueuedJob> jobsById = new HashMap<JobId, QueuedJob>(256);
    private final ClusteredJobDao clusteredJobDao;
    private final Lock lock;
    private final Condition awaken;
    private final Supplier<Boolean> pausedCondition;
    private final Long pausedCheckTimeMs;
    private volatile boolean closed;
    private volatile boolean paused;

    SchedulerQueueImpl(ClusteredJobDao clusteredJobDao, Supplier<Boolean> pausedCondition, @Nullable Long pausedCheckTimeMs) {
        this(clusteredJobDao, new ReentrantLock(), pausedCondition, pausedCheckTimeMs);
    }

    @VisibleForTesting
    SchedulerQueueImpl(ClusteredJobDao clusteredJobDao, Lock lock, Supplier<Boolean> pausedCondition, @Nullable Long pausedCheckTimeMs) {
        this.clusteredJobDao = Objects.requireNonNull(clusteredJobDao, "clusteredJobDao");
        this.lock = Objects.requireNonNull(lock, "lock");
        this.awaken = lock.newCondition();
        this.pausedCondition = Objects.requireNonNull(pausedCondition);
        this.pausedCheckTimeMs = Optional.ofNullable(pausedCheckTimeMs).orElse(60000L);
    }

    @Override
    public void add(QueuedJob job) throws SchedulerQueue.SchedulerShutdownException {
        QueuedJob replaced;
        Objects.requireNonNull(job, "job");
        this.lock.lock();
        try {
            replaced = this.addJobUnderLock(job);
        }
        finally {
            this.lock.unlock();
        }
        LOG.debug("add job={} replaced={}", (Object)job, (Object)replaced);
    }

    @GuardedBy(value="lock")
    private QueuedJob addJobUnderLock(QueuedJob job) throws SchedulerQueue.SchedulerShutdownException {
        this.ensureOpen();
        QueuedJob replaced = this.addOrReplaceJob(job);
        if (!this.isPaused() && this.queue.peek() == job) {
            this.awaken.signalAll();
        }
        return replaced;
    }

    @GuardedBy(value="lock")
    private QueuedJob addOrReplaceJob(JobId jobId, long deadline) {
        return this.addOrReplaceJob(new QueuedJob(jobId, deadline));
    }

    @GuardedBy(value="lock")
    private QueuedJob addOrReplaceJob(QueuedJob job) {
        QueuedJob replaced = this.jobsById.put(job.getJobId(), job);
        if (replaced != null) {
            this.queue.remove(replaced);
        }
        this.queue.add(job);
        return replaced;
    }

    @Override
    public Map<JobId, Date> refreshClusteredJobs() {
        this.lock.lock();
        try {
            Map<JobId, Date> map = this.refreshClusteredJobsUnderLock();
            return map;
        }
        finally {
            this.lock.unlock();
        }
    }

    private boolean isPaused() {
        return this.paused || this.pausedCondition.get() != false;
    }

    @GuardedBy(value="lock")
    private Map<JobId, Date> refreshClusteredJobsUnderLock() {
        if (this.closed) {
            return ImmutableMap.of();
        }
        long originalDeadline = this.nextJobDeadline();
        Map<JobId, Date> jobs = this.refreshClusteredJobsFromDao();
        if (!this.isPaused() && this.nextJobDeadline() < originalDeadline) {
            this.awaken.signalAll();
        }
        return jobs;
    }

    @GuardedBy(value="lock")
    private Map<JobId, Date> refreshClusteredJobsFromDao() {
        Map<JobId, Date> jobs = this.clusteredJobDao.refresh();
        for (Map.Entry<JobId, Date> entry : jobs.entrySet()) {
            JobId jobId = entry.getKey();
            Date nextRunTime = entry.getValue();
            if (jobId == null || nextRunTime == null) continue;
            this.addOrReplaceJob(jobId, nextRunTime.getTime());
        }
        return jobs;
    }

    @GuardedBy(value="lock")
    private long nextJobDeadline() {
        QueuedJob nextJob = this.queue.peek();
        return nextJob != null ? nextJob.getDeadline() : Long.MAX_VALUE;
    }

    @Override
    @Nullable
    public QueuedJob remove(JobId jobId) {
        QueuedJob removed;
        Objects.requireNonNull(jobId, "jobId");
        this.lock.lock();
        try {
            removed = this.removeUnderLock(jobId);
        }
        finally {
            this.lock.unlock();
        }
        LOG.debug("remove jobId={} removed={}", (Object)jobId, (Object)removed);
        return removed;
    }

    @GuardedBy(value="lock")
    private QueuedJob removeUnderLock(JobId jobId) {
        QueuedJob removed = this.jobsById.remove(jobId);
        if (removed != null) {
            this.queue.remove(removed);
        }
        return removed;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        this.lock.lock();
        try {
            this.closeUnderLock();
        }
        finally {
            this.lock.unlock();
        }
    }

    @GuardedBy(value="lock")
    private void closeUnderLock() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.jobsById.clear();
        this.queue.clear();
        this.awaken.signalAll();
    }

    @Override
    public void pause() throws SchedulerQueue.SchedulerShutdownException {
        this.lock.lock();
        try {
            this.ensureOpen();
            this.paused = true;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void resume() throws SchedulerQueue.SchedulerShutdownException {
        this.lock.lock();
        try {
            this.ensureOpen();
            if (this.paused) {
                this.paused = false;
                this.awaken.signalAll();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    @Nullable
    public QueuedJob take() throws InterruptedException {
        while (!this.closed) {
            QueuedJob nextJob;
            this.lock.lock();
            try {
                nextJob = this.takeUnderLock();
            }
            finally {
                this.lock.unlock();
            }
            if (nextJob != null) {
                LOG.debug("take: {}", (Object)nextJob);
                return nextJob;
            }
            LOG.debug("take: null (loop)");
        }
        LOG.debug("take: null (closed)");
        return null;
    }

    @Nullable
    @GuardedBy(value="lock")
    private QueuedJob takeUnderLock() throws InterruptedException {
        QueuedJob nextJob = this.queue.peek();
        if (nextJob == null || this.isPaused()) {
            this.awaken.await(this.pausedCheckTimeMs, TimeUnit.MILLISECONDS);
            return null;
        }
        long timeleft = nextJob.getDeadline() - this.now();
        if (timeleft <= 0L) {
            this.jobsById.remove(nextJob.getJobId());
            return this.queue.poll();
        }
        this.awaken.await(timeleft, TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public List<QueuedJob> getPendingJobs() {
        ArrayList<QueuedJob> list = new ArrayList<QueuedJob>(256);
        this.lock.lock();
        try {
            list.addAll(this.queue);
        }
        finally {
            this.lock.unlock();
        }
        Collections.sort(list);
        return ImmutableList.copyOf(list);
    }

    @Override
    public int getPendingJobsCount() {
        this.lock.lock();
        try {
            int n = this.queue.size();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    @VisibleForTesting
    long now() {
        return System.currentTimeMillis();
    }

    private void ensureOpen() throws SchedulerQueue.SchedulerShutdownException {
        if (this.closed) {
            throw new SchedulerQueue.SchedulerShutdownException();
        }
    }
}

