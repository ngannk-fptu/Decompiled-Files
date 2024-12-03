/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao.utils;

import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public final class DaoUtils {
    private DaoUtils() {
    }

    public static String getNodeId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            return "N/A";
        }
    }

    public static Date pastTimeDownToDurationFromNow(Duration duration, Clock clock) {
        return Date.from(LocalDateTime.now(clock).minus(duration).atZone(clock.getZone()).toInstant());
    }

    public static Date getUtcStartTime(RateLimitingReportSearchRequest searchRequest) {
        return Objects.nonNull(searchRequest.getStartTime()) ? Date.from(searchRequest.getStartTime().toInstant()) : new Date(0L);
    }

    public static Date getUtcFinishTime(RateLimitingReportSearchRequest searchRequest, Clock clock) {
        return Objects.nonNull(searchRequest.getFinishTime()) ? Date.from(searchRequest.getFinishTime().toInstant()) : Date.from(LocalDateTime.now(clock).atZone(clock.getZone()).toInstant());
    }
}

