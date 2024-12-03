/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.schedule;

import com.atlassian.scheduler.config.JobId;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class ScheduledJobKey
implements Serializable {
    private static final long serialVersionUID = 1300657235588420525L;
    private final String group;
    private final String jobId;

    public ScheduledJobKey(String jobId) {
        this.group = "DEFAULT";
        this.jobId = jobId;
    }

    public ScheduledJobKey(String group, String jobId) {
        this.group = group;
        this.jobId = (String)StringUtils.defaultIfEmpty((CharSequence)jobId, (CharSequence)"DEFAULT_JOB_ID");
    }

    public String getGroup() {
        return this.group;
    }

    public String getJobId() {
        return this.jobId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduledJobKey that = (ScheduledJobKey)o;
        return Objects.equals(this.group, that.group) && Objects.equals(this.jobId, that.jobId);
    }

    public int hashCode() {
        return Objects.hash(this.group, this.jobId);
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return StringUtils.join((Object[])new Object[]{this.group, this.jobId}, (char)'#');
    }

    public static ScheduledJobKey valueOf(JobId jobId) {
        if (jobId == null) {
            return null;
        }
        return new ScheduledJobKey(jobId.toString());
    }
}

