/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.history;

import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import java.util.Optional;
import java.util.function.Function;

public interface RateLimitHistoryReportResultMapper
extends Function<UserRateLimitingReport, Optional<RateLimitingReportSearchResult>> {
}

