/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.core.status.RunDetailsImpl
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.NotExportable;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.status.RunDetailsImpl;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;

public class SchedulerRunDetails
extends ConfluenceEntityObject
implements NotExportable {
    private String jobId;
    private Date startTime;
    private long duration;
    private char outcome;
    private String message;

    public static SchedulerRunDetails fromRunDetails(JobId jobId, RunDetails runDetails) {
        SchedulerRunDetails ret = new SchedulerRunDetails();
        ret.jobId = jobId.toString();
        ret.startTime = runDetails.getStartTime();
        ret.duration = runDetails.getDurationInMillis();
        ret.outcome = SchedulerRunDetails.runOutcomeToChar(runDetails.getRunOutcome());
        ret.message = runDetails.getMessage();
        return ret;
    }

    public RunDetails toRunDetails() {
        RunOutcome runOutcome = SchedulerRunDetails.charToRunOutcome(this.outcome);
        return new RunDetailsImpl(this.startTime, runOutcome, this.duration, this.message);
    }

    public static char runOutcomeToChar(RunOutcome runOutcome) {
        switch (runOutcome) {
            case SUCCESS: {
                return 'S';
            }
            case UNAVAILABLE: {
                return 'U';
            }
            case ABORTED: {
                return 'A';
            }
            case FAILED: {
                return 'F';
            }
        }
        throw new IllegalArgumentException("Unknown RunOutcome: " + runOutcome + "; it should be SUCCESS, UNAVAILABLE, ABORTED, or FAILED");
    }

    public static RunOutcome charToRunOutcome(char outcome) {
        switch (outcome) {
            case 'S': {
                return RunOutcome.SUCCESS;
            }
            case 'U': {
                return RunOutcome.UNAVAILABLE;
            }
            case 'A': {
                return RunOutcome.ABORTED;
            }
            case 'F': {
                return RunOutcome.FAILED;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + outcome + " to " + RunOutcome.class);
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public char getOutcome() {
        return this.outcome;
    }

    public void setOutcome(char outcome) {
        this.outcome = outcome;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

