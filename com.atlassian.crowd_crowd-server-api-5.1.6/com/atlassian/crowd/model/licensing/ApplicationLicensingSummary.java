/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.licensing;

import com.atlassian.crowd.model.application.Application;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;

public class ApplicationLicensingSummary {
    private final Date generatedOn;
    private final long versionId;
    private final Application application;
    private final String applicationSubtype;
    private final int totalUsers;
    private final int totalUsersFromLocalCrowd;
    private final boolean active;
    private final int maximumUserLimit;
    private final int totalDirectories;

    protected ApplicationLicensingSummary(Date generatedOn, long versionId, Application application, String applicationSubtype, int totalUsers, int totalUsersFromLocalCrowd, boolean active, int maximumUserLimit, int totalDirectories) {
        this.generatedOn = Objects.requireNonNull(generatedOn);
        this.versionId = versionId;
        this.application = Objects.requireNonNull(application);
        this.applicationSubtype = applicationSubtype;
        this.totalUsers = totalUsers;
        this.totalUsersFromLocalCrowd = totalUsersFromLocalCrowd;
        this.active = active;
        this.maximumUserLimit = maximumUserLimit;
        this.totalDirectories = totalDirectories;
    }

    public Date getGeneratedOn() {
        return this.generatedOn;
    }

    public long getVersionId() {
        return this.versionId;
    }

    public Application getApplication() {
        return this.application;
    }

    public String getApplicationSubtype() {
        return this.applicationSubtype;
    }

    public int getTotalUsers() {
        return this.totalUsers;
    }

    public int getTotalUsersFromLocalCrowd() {
        return this.totalUsersFromLocalCrowd;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getMaximumUserLimit() {
        return this.maximumUserLimit;
    }

    public int getTotalDirectories() {
        return this.totalDirectories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationLicensingSummary data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationLicensingSummary that = (ApplicationLicensingSummary)o;
        return Objects.equals(this.getGeneratedOn(), that.getGeneratedOn()) && Objects.equals(this.getVersionId(), that.getVersionId()) && Objects.equals(this.getApplication(), that.getApplication()) && Objects.equals(this.getApplicationSubtype(), that.getApplicationSubtype()) && Objects.equals(this.getTotalUsers(), that.getTotalUsers()) && Objects.equals(this.getTotalUsersFromLocalCrowd(), that.getTotalUsersFromLocalCrowd()) && Objects.equals(this.isActive(), that.isActive()) && Objects.equals(this.getMaximumUserLimit(), that.getMaximumUserLimit()) && Objects.equals(this.getTotalDirectories(), that.getTotalDirectories());
    }

    public int hashCode() {
        return Objects.hash(this.getGeneratedOn(), this.getVersionId(), this.getApplication(), this.getApplicationSubtype(), this.getTotalUsers(), this.getTotalUsersFromLocalCrowd(), this.isActive(), this.getMaximumUserLimit(), this.getTotalDirectories());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("generatedOn", (Object)this.getGeneratedOn()).add("versionId", this.getVersionId()).add("application", (Object)this.getApplication()).add("applicationSubtype", (Object)this.getApplicationSubtype()).add("totalUsers", this.getTotalUsers()).add("totalUsersFromLocalCrowd", this.getTotalUsersFromLocalCrowd()).add("active", this.isActive()).add("maximumUserLimit", this.getMaximumUserLimit()).add("totalDirectories", this.getTotalDirectories()).toString();
    }

    public static final class Builder {
        private Date generatedOn;
        private long versionId;
        private Application application;
        private String applicationSubtype;
        private int totalUsers;
        private int totalUsersFromLocalCrowd;
        private boolean active;
        private int maximumUserLimit;
        private int totalDirectories;

        private Builder() {
        }

        private Builder(ApplicationLicensingSummary initialData) {
            this.generatedOn = initialData.getGeneratedOn();
            this.versionId = initialData.getVersionId();
            this.application = initialData.getApplication();
            this.applicationSubtype = initialData.getApplicationSubtype();
            this.totalUsers = initialData.getTotalUsers();
            this.totalUsersFromLocalCrowd = initialData.getTotalUsersFromLocalCrowd();
            this.active = initialData.isActive();
            this.maximumUserLimit = initialData.getMaximumUserLimit();
            this.totalDirectories = initialData.getTotalDirectories();
        }

        public Builder setGeneratedOn(Date generatedOn) {
            this.generatedOn = generatedOn;
            return this;
        }

        public Builder setVersionId(long versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder setApplication(Application application) {
            this.application = application;
            return this;
        }

        public Builder setApplicationSubtype(String applicationSubtype) {
            this.applicationSubtype = applicationSubtype;
            return this;
        }

        public Builder setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public Builder setTotalUsersFromLocalCrowd(int totalUsersFromLocalCrowd) {
            this.totalUsersFromLocalCrowd = totalUsersFromLocalCrowd;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setMaximumUserLimit(int maximumUserLimit) {
            this.maximumUserLimit = maximumUserLimit;
            return this;
        }

        public Builder setTotalDirectories(int totalDirectories) {
            this.totalDirectories = totalDirectories;
            return this;
        }

        public ApplicationLicensingSummary build() {
            return new ApplicationLicensingSummary(this.generatedOn, this.versionId, this.application, this.applicationSubtype, this.totalUsers, this.totalUsersFromLocalCrowd, this.active, this.maximumUserLimit, this.totalDirectories);
        }
    }
}

