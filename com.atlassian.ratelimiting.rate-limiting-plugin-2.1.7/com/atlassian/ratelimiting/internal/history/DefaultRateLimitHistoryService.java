/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.internal.history;

import com.atlassian.ratelimiting.dao.UserRateLimitCounterDao;
import com.atlassian.ratelimiting.history.RateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.history.RateLimitingReportService;
import com.atlassian.ratelimiting.page.Page;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class DefaultRateLimitHistoryService
implements RateLimitingReportService {
    private final UserRateLimitCounterDao userRateLimitCounterDao;
    private final RateLimitHistoryReportResultMapper historyReportResultMapper;

    public DefaultRateLimitHistoryService(UserRateLimitCounterDao counterDao, RateLimitHistoryReportResultMapper historyReportResultMapper) {
        this.userRateLimitCounterDao = counterDao;
        this.historyReportResultMapper = historyReportResultMapper;
    }

    @Override
    public Page<RateLimitingReportSearchResult> getHistoryReport(@Nonnull RateLimitingReportSearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest);
        Objects.requireNonNull(searchRequest.getPageRequest());
        return this.userRateLimitCounterDao.getAggregateCounts(searchRequest).map(this.historyReportResultMapper).filter(Optional::isPresent).map(Optional::get);
    }
}

