/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.diagnostics.low-file-descriptor-count")
public class LowFileDescriptorCountAnalyticsEvent {
    private Long maxCount;
    private Long openCount;
    private Long requiredFreeCount;

    public LowFileDescriptorCountAnalyticsEvent(Long maxCount, Long openCount, Long requiredFreeCount) {
        this.maxCount = maxCount;
        this.openCount = openCount;
        this.requiredFreeCount = requiredFreeCount;
    }

    public Long getMaxCount() {
        return this.maxCount;
    }

    public Long getOpenCount() {
        return this.openCount;
    }

    public Long getRequiredFreeCount() {
        return this.requiredFreeCount;
    }
}

