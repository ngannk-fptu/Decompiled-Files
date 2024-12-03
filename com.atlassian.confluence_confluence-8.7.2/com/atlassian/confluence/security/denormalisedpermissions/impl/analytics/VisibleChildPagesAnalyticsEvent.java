/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@AsynchronousPreferred
@EventName(value="confluence.denormalised-permissions-service.visible-child-pages")
public class VisibleChildPagesAnalyticsEvent {
    private final int parentPagesCount;
    private final boolean checkInheritedPermissions;
    private final long childPagesCount;
    private final boolean isFallbackServiceUsed;
    private final long durationMillisecond;
    private final boolean permissionsExempt;
    private final Integer amountOfSids;

    public VisibleChildPagesAnalyticsEvent(int parentPagesCount, boolean checkInheritedPermissions, long childPagesCount, boolean isFallbackServiceUsed, long durationMillisecond, boolean permissionsExempt) {
        this(parentPagesCount, checkInheritedPermissions, childPagesCount, isFallbackServiceUsed, durationMillisecond, permissionsExempt, null);
    }

    public VisibleChildPagesAnalyticsEvent(int parentPagesCount, boolean checkInheritedPermissions, long childPagesCount, boolean isFallbackServiceUsed, long durationMillisecond, boolean permissionsExempt, Integer amountOfSids) {
        this.parentPagesCount = parentPagesCount;
        this.checkInheritedPermissions = checkInheritedPermissions;
        this.childPagesCount = childPagesCount;
        this.isFallbackServiceUsed = isFallbackServiceUsed;
        this.durationMillisecond = durationMillisecond;
        this.permissionsExempt = permissionsExempt;
        this.amountOfSids = amountOfSids;
    }

    @JsonProperty(value="parentPagesCount")
    public int getParentPagesCount() {
        return this.parentPagesCount;
    }

    @JsonProperty(value="checkInheritedPermissions")
    public boolean isCheckInheritedPermissions() {
        return this.checkInheritedPermissions;
    }

    @JsonProperty(value="childPagesCount")
    public long getChildPagesCount() {
        return this.childPagesCount;
    }

    @JsonProperty(value="isFallbackServiceUsed")
    public boolean isFallbackServiceUsed() {
        return this.isFallbackServiceUsed;
    }

    @JsonProperty(value="durationMillisecond")
    public long getDurationMillisecond() {
        return this.durationMillisecond;
    }

    @JsonProperty(value="permissionsExempt")
    public boolean isPermissionsExempt() {
        return this.permissionsExempt;
    }

    @JsonProperty(value="amountOfSids")
    public Integer getAmountOfSids() {
        return this.amountOfSids;
    }
}

