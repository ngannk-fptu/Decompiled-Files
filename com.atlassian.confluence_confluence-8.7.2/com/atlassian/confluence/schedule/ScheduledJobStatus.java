/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  org.apache.commons.collections.Buffer
 *  org.apache.commons.collections.BufferUtils
 *  org.apache.commons.collections.buffer.CircularFifoBuffer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobKey;
import com.atlassian.scheduler.config.JobId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledJobStatus
implements Serializable {
    private static final long serialVersionUID = 5111787748030958238L;
    private static final Logger log = LoggerFactory.getLogger(ScheduledJobStatus.class);
    public static final int MAX_HISTORY = 100;
    @Deprecated
    private final ScheduledJobKey scheduledJobKey = null;
    private final JobId jobId;
    private final AtomicReference<Date> lastExecution = new AtomicReference();
    private final AtomicReference<Date> nextExecution = new AtomicReference();
    private final Buffer history = BufferUtils.synchronizedBuffer((Buffer)new CircularFifoBuffer(100));
    private final AtomicReference<ExecutionStatus> status = new AtomicReference();

    public ScheduledJobStatus(JobId jobId, List<ScheduledJobHistory> existingHistory) {
        this.jobId = jobId;
        if (existingHistory != null) {
            this.history.addAll(existingHistory);
            if (existingHistory.size() > 0) {
                ScheduledJobHistory lastEvent = existingHistory.stream().max(Comparator.comparing(ScheduledJobHistory::getStartDate)).get();
                this.lastExecution.set(lastEvent.getStartDate());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("ScheduledJobStatus({},[{}]{})", new Object[]{jobId, existingHistory == null ? 0 : existingHistory.size(), existingHistory});
        }
    }

    public JobId getJobId() {
        return this.scheduledJobKey != null ? JobId.of((String)this.scheduledJobKey.getJobId()) : this.jobId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ScheduledJobHistory> getHistory() {
        Buffer buffer = this.history;
        synchronized (buffer) {
            return new ArrayList<ScheduledJobHistory>((Collection<ScheduledJobHistory>)this.history);
        }
    }

    public void addHistory(ScheduledJobHistory newHistory) {
        this.history.add((Object)newHistory);
        this.lastExecution.set(newHistory.getStartDate());
    }

    public ExecutionStatus getStatus() {
        return this.status.get();
    }

    public void setStatus(ExecutionStatus status) {
        this.status.set(status);
    }

    public Date getLastExecution() {
        return this.lastExecution.get();
    }

    public Date getNextExecution() {
        return this.nextExecution.get();
    }

    public void setNextExecution(Date nextExecution) {
        this.nextExecution.set(nextExecution);
    }

    public long getAverageRunningTime() {
        List<ScheduledJobHistory> currentHistory = this.getHistory();
        double total = 0.0;
        for (ScheduledJobHistory jh : currentHistory) {
            total += (double)jh.getDuration();
        }
        int historySize = currentHistory.size();
        double avg = 0.0;
        if (historySize > 0) {
            avg = total / (double)historySize;
        }
        return (long)avg;
    }

    public boolean isEnabled() {
        return !ExecutionStatus.DISABLED.equals((Object)this.getStatus()) && !ExecutionStatus.DISABLED_MANUALLY_RUNNING.equals((Object)this.getStatus());
    }

    public boolean isDisabled() {
        return ExecutionStatus.DISABLED.equals((Object)this.getStatus()) || ExecutionStatus.DISABLED_MANUALLY_RUNNING.equals((Object)this.getStatus());
    }

    public boolean isManuallyRunnable() {
        ExecutionStatus currentStatus = this.getStatus();
        return ExecutionStatus.DISABLED == currentStatus || ExecutionStatus.SCHEDULED == currentStatus;
    }
}

