/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.ScheduledJob;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractScheduledJob
implements ScheduledJob {
    private final JobRunner jobRunner;
    private final JobConfig jobConfig;
    private final boolean clusteredOnly;

    protected AbstractScheduledJob(JobRunner jobRunner, JobConfig jobConfig, boolean clusteredOnly) {
        this.jobRunner = jobRunner;
        this.jobConfig = jobConfig;
        this.clusteredOnly = clusteredOnly;
    }

    @Override
    public JobRunner getJobRunner() {
        return this.jobRunner;
    }

    @Override
    public JobConfig getJobConfig() {
        return this.jobConfig;
    }

    @Override
    public boolean isClusteredOnly() {
        return this.clusteredOnly;
    }

    public String toString() {
        ToStringBuilder toString = new ToStringBuilder((Object)this);
        toString.append("jobRunner", (Object)this.getJobRunner().getClass().getName());
        toString.append("jobConfig", (Object)this.getJobConfig());
        toString.append("clusteredOnly", this.isClusteredOnly());
        return toString.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractScheduledJob that = (AbstractScheduledJob)o;
        if (this.getJobRunner() != that.getJobRunner()) {
            return false;
        }
        if (this.getJobConfig() != null ? !this.getJobConfig().equals((Object)that.getJobConfig()) : that.getJobConfig() != null) {
            return false;
        }
        return this.isClusteredOnly() == that.isClusteredOnly();
    }

    public int hashCode() {
        int result = this.getJobRunner() != null ? this.getJobRunner().hashCode() : 0;
        result = 31 * result + (this.getJobConfig() != null ? this.getJobConfig().hashCode() : 0);
        result = 31 * result + (this.clusteredOnly ? 1 : 0);
        return result;
    }
}

