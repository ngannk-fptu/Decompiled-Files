/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.internal;

import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.core.NotExportable;
import com.atlassian.core.bean.EntityObject;
import java.util.Objects;

public class ZduStatusEntity
extends EntityObject
implements NotExportable {
    private ZduStatus.State state;
    private String originalClusterVersion;
    private int originalBuildNumber;

    ZduStatusEntity() {
    }

    public ZduStatusEntity(ZduStatus.State state, String originalClusterVersion, int originalBuildNumber) {
        this.state = state;
        this.originalClusterVersion = originalClusterVersion;
        this.originalBuildNumber = originalBuildNumber;
    }

    public int getOriginalBuildNumber() {
        return this.originalBuildNumber;
    }

    public String getOriginalClusterVersion() {
        return this.originalClusterVersion;
    }

    public void setOriginalBuildNumber(int originalBuildNumber) {
        this.originalBuildNumber = originalBuildNumber;
    }

    public void setOriginalClusterVersion(String originalClusterVersion) {
        this.originalClusterVersion = originalClusterVersion;
    }

    public ZduStatus.State getState() {
        return this.state;
    }

    public void setState(ZduStatus.State state) {
        this.state = state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ZduStatusEntity that = (ZduStatusEntity)o;
        return this.originalBuildNumber == that.originalBuildNumber && this.state == that.state && Objects.equals(this.originalClusterVersion, that.originalClusterVersion);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode(), this.state, this.originalClusterVersion, this.originalBuildNumber});
    }
}

