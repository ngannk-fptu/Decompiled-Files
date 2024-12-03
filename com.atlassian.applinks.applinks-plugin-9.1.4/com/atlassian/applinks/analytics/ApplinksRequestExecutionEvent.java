/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.applinks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Objects;

@EventName(value="applinks.request.execution")
public class ApplinksRequestExecutionEvent {
    private final Long approxRequestSize;
    private final Long approxResponseSize;
    private final String remoteApplicationId;

    public ApplinksRequestExecutionEvent(Long approxRequestSize, Long approxResponseSize, String remoteApplicationId) {
        this.approxRequestSize = approxRequestSize;
        this.approxResponseSize = approxResponseSize;
        this.remoteApplicationId = Objects.requireNonNull(remoteApplicationId);
    }

    public Long getApproxRequestSize() {
        return this.approxRequestSize;
    }

    public Long getApproxResponseSize() {
        return this.approxResponseSize;
    }

    public String getRemoteApplicationId() {
        return this.remoteApplicationId;
    }
}

