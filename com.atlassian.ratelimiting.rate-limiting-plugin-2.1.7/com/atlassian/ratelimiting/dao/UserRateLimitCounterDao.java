/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dao.UserRateLimitCounter;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.ratelimiting.page.Page;
import java.time.Duration;
import javax.annotation.Nonnull;

public interface UserRateLimitCounterDao {
    @Nonnull
    public UserRateLimitCounter create(@Nonnull UserRateLimitCounter var1);

    public long deleteOlderThan(@Nonnull Duration var1);

    public Page<UserRateLimitingReport> getAggregateCounts(@Nonnull RateLimitingReportSearchRequest var1);
}

