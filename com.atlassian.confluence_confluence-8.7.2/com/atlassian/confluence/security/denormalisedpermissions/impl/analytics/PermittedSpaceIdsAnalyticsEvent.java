/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.denormalised-permissions-service.get-permitted-space-ids")
public class PermittedSpaceIdsAnalyticsEvent {
    private final long duration;
    private final int numberOfSids;
    private final boolean permissionExempt;
    private final int inputNumberOfSpaces;
    private final int visibleNumberOfSpaces;
    private final int visibleNumberOfSpacesBeforeRecheckingPermissions;
    private final int numberOfRecentlyChangedSpaces;

    public PermittedSpaceIdsAnalyticsEvent(long duration, int numberOfSids, boolean permissionExempt, int inputNumberOfSpaces, int visibleNumberOfSpaces, int visibleNumberOfSpacesBeforeRecheckingPermissions, int numberOfRecentlyChangedSpaces) {
        this.duration = duration;
        this.numberOfSids = numberOfSids;
        this.permissionExempt = permissionExempt;
        this.inputNumberOfSpaces = inputNumberOfSpaces;
        this.visibleNumberOfSpaces = visibleNumberOfSpaces;
        this.visibleNumberOfSpacesBeforeRecheckingPermissions = visibleNumberOfSpacesBeforeRecheckingPermissions;
        this.numberOfRecentlyChangedSpaces = numberOfRecentlyChangedSpaces;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getNumberOfSids() {
        return this.numberOfSids;
    }

    public boolean isPermissionExempt() {
        return this.permissionExempt;
    }

    public int getInputNumberOfSpaces() {
        return this.inputNumberOfSpaces;
    }

    public int getVisibleNumberOfSpaces() {
        return this.visibleNumberOfSpaces;
    }

    public int getVisibleNumberOfSpacesBeforeRecheckingPermissions() {
        return this.visibleNumberOfSpacesBeforeRecheckingPermissions;
    }

    public int getNumberOfRecentlyChangedSpaces() {
        return this.numberOfRecentlyChangedSpaces;
    }
}

