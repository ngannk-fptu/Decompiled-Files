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
@EventName(value="confluence.denormalised-permissions-service.visible-top-level-pages")
public class VisibleTopLevelPagesAnalyticsEvent {
    private final int visiblePagesCount;
    private final boolean isFallbackServiceUsed;
    private final long durationMillisecond;
    private final boolean permissionsExempt;
    private final Integer amountOfSids;

    public VisibleTopLevelPagesAnalyticsEvent(int visiblePagesCount, boolean isFallbackServiceUsed, long durationMillisecond, boolean permissionsExempt) {
        this(visiblePagesCount, isFallbackServiceUsed, durationMillisecond, permissionsExempt, null);
    }

    public VisibleTopLevelPagesAnalyticsEvent(int visiblePagesCount, boolean isFallbackServiceUsed, long durationMillisecond, boolean permissionsExempt, Integer amountOfSids) {
        this.visiblePagesCount = visiblePagesCount;
        this.isFallbackServiceUsed = isFallbackServiceUsed;
        this.durationMillisecond = durationMillisecond;
        this.permissionsExempt = permissionsExempt;
        this.amountOfSids = amountOfSids;
    }

    @JsonProperty(value="visiblePagesCount")
    public int getVisiblePagesCount() {
        return this.visiblePagesCount;
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

