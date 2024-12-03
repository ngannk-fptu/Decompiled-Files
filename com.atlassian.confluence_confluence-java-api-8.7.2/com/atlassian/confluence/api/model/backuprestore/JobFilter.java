/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class JobFilter {
    private final JobScope jobScope;
    private final JobOperation jobOperation;
    private final Set<JobState> jobStates;
    private final String owner;
    private final Instant fromDate;
    private final Instant toDate;
    private final String spaceKey;
    private final int limit;

    private JobFilter(Builder builder) {
        this.jobScope = builder.jobScope;
        this.jobOperation = builder.jobOperation;
        this.jobStates = builder.jobStates;
        this.owner = builder.owner;
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.spaceKey = builder.spaceKey;
        this.limit = builder.limit;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }

    public JobOperation getJobOperation() {
        return this.jobOperation;
    }

    public Set<JobState> getJobStates() {
        return this.jobStates;
    }

    public String getOwner() {
        return this.owner;
    }

    public Instant getFromDate() {
        return this.fromDate;
    }

    public Instant getToDate() {
        return this.toDate;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public int getLimit() {
        return this.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private JobScope jobScope;
        private JobOperation jobOperation;
        private final Set<JobState> jobStates = new HashSet<JobState>();
        private String owner;
        private Instant fromDate;
        private Instant toDate;
        private String spaceKey;
        private int limit;

        private Builder() {
        }

        public JobFilter build() {
            this.validate();
            return new JobFilter(this);
        }

        public Builder setJobScope(JobScope jobScope) {
            this.jobScope = jobScope;
            return this;
        }

        public Builder setJobOperation(JobOperation jobOperation) {
            this.jobOperation = jobOperation;
            return this;
        }

        public Builder addJobState(JobState jobState) {
            this.jobStates.add(jobState);
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder setFromDate(Instant fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder setToDate(Instant toDate) {
            this.toDate = toDate;
            return this;
        }

        public Builder setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        private void validate() {
            if (this.fromDate != null && this.toDate != null && this.fromDate.isAfter(this.toDate)) {
                throw new IllegalArgumentException("`fromDate` should be smaller than or equal to `toDate`");
            }
        }
    }
}

