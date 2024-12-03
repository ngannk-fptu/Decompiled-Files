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
@EventName(value="confluence.denormalised-permissions-service.get-space-keys-with-statuses")
public class SpaceKeysWithStatusesAnalyticsEvent {
    private final int permittedSpacesCount;
    private final int nonPermittedSpacesCount;
    private final boolean isFallbackServiceUsed;
    private final boolean isLegacyCodeUsed;
    private final long permittedSpacesDurationMs;
    private final long nonPermittedSpacesDurationMs;
    private final boolean permissionsExempt;
    private final Integer amountOfSids;

    public SpaceKeysWithStatusesAnalyticsEvent(int permittedSpacesCount, int nonPermittedSpacesCount, boolean isFallbackServiceUsed, boolean isLegacyCodeUsed, long permittedSpacesDurationMs, long nonPermittedSpacesDurationMs, boolean permissionsExempt, Integer amountOfSids) {
        this.permittedSpacesCount = permittedSpacesCount;
        this.nonPermittedSpacesCount = nonPermittedSpacesCount;
        this.isFallbackServiceUsed = isFallbackServiceUsed;
        this.isLegacyCodeUsed = isLegacyCodeUsed;
        this.permittedSpacesDurationMs = permittedSpacesDurationMs;
        this.nonPermittedSpacesDurationMs = nonPermittedSpacesDurationMs;
        this.permissionsExempt = permissionsExempt;
        this.amountOfSids = amountOfSids;
    }

    public boolean isFallbackServiceUsed() {
        return this.isFallbackServiceUsed;
    }

    public boolean isLegacyCodeUsed() {
        return this.isLegacyCodeUsed;
    }

    public long getPermittedSpacesDurationMs() {
        return this.permittedSpacesDurationMs;
    }

    public long getNonPermittedSpacesDurationMs() {
        return this.nonPermittedSpacesDurationMs;
    }

    public boolean isPermissionsExempt() {
        return this.permissionsExempt;
    }

    public Integer getAmountOfSids() {
        return this.amountOfSids;
    }

    public int getPermittedSpacesCount() {
        return this.permittedSpacesCount;
    }

    public int getNonPermittedSpacesCount() {
        return this.nonPermittedSpacesCount;
    }
}

