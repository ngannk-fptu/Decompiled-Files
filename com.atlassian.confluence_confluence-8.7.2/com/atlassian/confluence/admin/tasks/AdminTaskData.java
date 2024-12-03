/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.tasks;

import java.io.Serializable;
import java.util.Date;

public class AdminTaskData
implements Serializable {
    public Date completedAt;
    public String completedByUsername;
    public String completedByFullName;
    public String signedOffValue;

    public AdminTaskData() {
        this.completedAt = null;
        this.completedByUsername = null;
        this.completedByFullName = null;
        this.signedOffValue = null;
    }

    public AdminTaskData(Date completedAt, String completedByUsername, String completedByFullName, String signedOffValue) {
        this.completedAt = completedAt;
        this.completedByFullName = completedByFullName;
        this.completedByUsername = completedByUsername;
        this.signedOffValue = signedOffValue;
    }

    public boolean isCompleted() {
        return this.completedAt != null;
    }

    public Date getCompletedAt() {
        return this.completedAt;
    }

    public String getCompletedByFullName() {
        return this.completedByFullName;
    }

    public String getCompletedByUsername() {
        return this.completedByUsername;
    }

    public String getSignedOffValue() {
        return this.signedOffValue;
    }
}

