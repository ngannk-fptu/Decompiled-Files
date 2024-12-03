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
@EventName(value="confluence.denormalised-permissions-service.get-permitted-spaces")
public class PermittedSpacesAnalyticsEvent {
    private final int receivedSpacesCount;
    private final int requestedSpacesCount;
    private final boolean isFallbackServiceUsed;
    private final boolean isLegacyCodeUsed;
    private final long durationMillisecond;
    private final boolean permissionsExempt;
    private final Integer amountOfSids;

    public PermittedSpacesAnalyticsEvent(int receivedSpacesCount, int requestedSpacesCount, boolean isFallbackServiceUsed, boolean isLegacyCodeUsed, long durationMillisecond, boolean permissionsExempt, Integer amountOfSids) {
        this.receivedSpacesCount = receivedSpacesCount;
        this.requestedSpacesCount = requestedSpacesCount;
        this.isFallbackServiceUsed = isFallbackServiceUsed;
        this.isLegacyCodeUsed = isLegacyCodeUsed;
        this.durationMillisecond = durationMillisecond;
        this.permissionsExempt = permissionsExempt;
        this.amountOfSids = amountOfSids;
    }

    public int getReceivedSpacesCount() {
        return this.receivedSpacesCount;
    }

    public int getRequestedSpacesCount() {
        return this.requestedSpacesCount;
    }

    public boolean isFallbackServiceUsed() {
        return this.isFallbackServiceUsed;
    }

    public long getDurationMillisecond() {
        return this.durationMillisecond;
    }

    public boolean isPermissionsExempt() {
        return this.permissionsExempt;
    }

    public Integer getAmountOfSids() {
        return this.amountOfSids;
    }

    public boolean isLegacyCodeUsed() {
        return this.isLegacyCodeUsed;
    }
}

