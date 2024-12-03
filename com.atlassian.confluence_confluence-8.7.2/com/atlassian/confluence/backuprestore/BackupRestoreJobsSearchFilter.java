/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

public class BackupRestoreJobsSearchFilter {
    private final Collection<JobState> jobStates;
    private final JobScope jobScope;
    private final JobOperation jobOperation;
    private final String spaceKey;
    private final String owner;
    private final Instant dateFrom;
    private final Instant dateTo;
    private final Integer limit;

    private BackupRestoreJobsSearchFilter(Collection<JobState> jobStates, String spaceKey, String owner, Instant dateFrom, Instant dateTo, Integer limit, JobScope jobScope, JobOperation jobOperation) {
        this.jobStates = jobStates;
        this.spaceKey = spaceKey;
        this.owner = owner;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.limit = limit;
        this.jobScope = jobScope;
        this.jobOperation = jobOperation;
    }

    public Collection<JobState> getJobStates() {
        return this.jobStates;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getOwner() {
        return this.owner;
    }

    public Instant getDateFrom() {
        return this.dateFrom;
    }

    public Instant getDateTo() {
        return this.dateTo;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }

    public JobOperation getJobOperation() {
        return this.jobOperation;
    }

    public static class Builder {
        private Collection<JobState> jobStates;
        private String spaceKey;
        private String owner;
        private Instant dateFrom;
        private Instant dateTo;
        private Integer limit;
        private JobScope jobScope;
        private JobOperation jobOperation;

        public Builder() {
            this.jobStates = Set.of();
        }

        public Builder(JobState jobState) {
            this.jobStates = ImmutableSet.of((Object)jobState);
        }

        public Builder(Collection<JobState> jobStates) {
            this.jobStates = ImmutableSet.copyOf(jobStates);
        }

        public Builder bySpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public Builder byOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder dateRange(Instant from, Instant to) {
            this.dateFrom = from;
            this.dateTo = to;
            return this;
        }

        public Builder setLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder setJobScope(JobScope jobScope) {
            this.jobScope = jobScope;
            return this;
        }

        public Builder setJobOperation(JobOperation jobOperation) {
            this.jobOperation = jobOperation;
            return this;
        }

        public BackupRestoreJobsSearchFilter build() {
            return new BackupRestoreJobsSearchFilter(this.jobStates, this.spaceKey, this.owner, this.dateFrom, this.dateTo, this.limit, this.jobScope, this.jobOperation);
        }
    }
}

