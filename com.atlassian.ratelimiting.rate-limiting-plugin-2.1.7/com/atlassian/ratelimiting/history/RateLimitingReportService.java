/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.history;

import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.page.Page;
import javax.annotation.Nonnull;

public interface RateLimitingReportService {
    public Page<RateLimitingReportSearchResult> getHistoryReport(@Nonnull RateLimitingReportSearchRequest var1);
}

