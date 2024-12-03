/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.core.bean.EntityObject;
import java.util.Date;

public class VersionHistory
extends EntityObject
implements NotExportable {
    private int buildNumber;
    private Date installationDate;
    private String versionTag;
    private boolean finalized;

    public VersionHistory() {
    }

    public VersionHistory(int buildNumber, Date installationDate) {
        this(buildNumber, installationDate, null);
    }

    public VersionHistory(int buildNumber, Date installationDate, String versionTag) {
        this.buildNumber = buildNumber;
        this.installationDate = installationDate;
        this.versionTag = versionTag;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public boolean isFinalized() {
        return this.finalized;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Date getInstallationDate() {
        return this.installationDate;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public String getVersionTag() {
        return this.versionTag;
    }

    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }
}

