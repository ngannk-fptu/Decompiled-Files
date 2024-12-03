/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.history;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.job.history.page.requested")
public class JobHistoryRequestedAnalyticEvent
extends AnalyticEvent {
    private final boolean isLastPage;
    private final int limit;
    private final int offset;
    private final int size;

    public JobHistoryRequestedAnalyticEvent(@Nonnull String pluginVersion, boolean isLastPage, int limit, int offset, int size) {
        super(pluginVersion);
        this.isLastPage = isLastPage;
        this.limit = limit;
        this.offset = offset;
        this.size = size;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isLastPage() {
        return this.isLastPage;
    }
}

