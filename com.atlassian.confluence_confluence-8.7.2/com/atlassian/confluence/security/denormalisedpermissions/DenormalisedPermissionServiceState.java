/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions;

public enum DenormalisedPermissionServiceState {
    DISABLED,
    INITIALISING,
    SHUTTING_DOWN,
    SERVICE_READY,
    STALE_DATA,
    ERROR;


    public boolean isDisablingAllowed() {
        return SERVICE_READY == this || STALE_DATA == this || INITIALISING == this;
    }

    public boolean isEnablingAllowed() {
        return DISABLED == this || ERROR == this;
    }
}

