/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;

public class DenormalisedServiceStateRecord
implements NotExportable {
    private ServiceType serviceType;
    private DenormalisedPermissionServiceState state;
    private long lastUpToDateTimestamp;

    public DenormalisedServiceStateRecord() {
    }

    public DenormalisedServiceStateRecord(ServiceType serviceType, DenormalisedPermissionServiceState state, long lastUpToDateTimestamp) {
        this.serviceType = serviceType;
        this.state = state;
        this.lastUpToDateTimestamp = lastUpToDateTimestamp;
    }

    public DenormalisedPermissionServiceState getState() {
        return this.state;
    }

    public void setState(DenormalisedPermissionServiceState state) {
        this.state = state;
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public long getLastUpToDateTimestamp() {
        return this.lastUpToDateTimestamp;
    }

    public void setLastUpToDateTimestamp(long lastUpToDateTimestamp) {
        this.lastUpToDateTimestamp = lastUpToDateTimestamp;
    }

    public static enum ServiceType {
        SPACE("Space"),
        CONTENT("Content");

        private final String displayName;

        private ServiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }
}

