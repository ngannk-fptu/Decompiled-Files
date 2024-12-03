/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.ApplicationSubtype
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.model.licensing;

import com.atlassian.crowd.model.ApplicationSubtype;
import com.atlassian.crowd.model.application.Application;
import java.util.Date;
import java.util.Objects;

public class LicensingSummary {
    private Long id;
    private Date generatedOn;
    private long versionId;
    private Application application;
    private ApplicationSubtype applicationSubtype;
    private int totalUsers;
    private int totalUsersFromLocalCrowd;
    private boolean active;
    private int maximumUserLimit;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGeneratedOn() {
        return this.generatedOn;
    }

    public void setGeneratedOn(Date generatedOn) {
        this.generatedOn = generatedOn;
    }

    public long getVersionId() {
        return this.versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public Application getApplication() {
        return this.application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationSubtype getApplicationSubtype() {
        return this.applicationSubtype;
    }

    public void setApplicationSubtype(ApplicationSubtype applicationSubtype) {
        this.applicationSubtype = applicationSubtype;
    }

    public int getTotalUsers() {
        return this.totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalUsersFromLocalCrowd() {
        return this.totalUsersFromLocalCrowd;
    }

    public void setTotalUsersFromLocalCrowd(int totalUsersFromLocalCrowd) {
        this.totalUsersFromLocalCrowd = totalUsersFromLocalCrowd;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMaximumUserLimit() {
        return this.maximumUserLimit;
    }

    public void setMaximumUserLimit(int maximumUserLimit) {
        this.maximumUserLimit = maximumUserLimit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LicensingSummary that = (LicensingSummary)o;
        return this.versionId == that.versionId && this.totalUsers == that.totalUsers && this.totalUsersFromLocalCrowd == that.totalUsersFromLocalCrowd && this.active == that.active && this.maximumUserLimit == that.maximumUserLimit && Objects.equals(this.id, that.id) && Objects.equals(this.generatedOn, that.generatedOn) && Objects.equals(this.application, that.application) && this.applicationSubtype == that.applicationSubtype;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.generatedOn, this.versionId, this.application, this.applicationSubtype, this.totalUsers, this.totalUsersFromLocalCrowd, this.active, this.maximumUserLimit);
    }
}

