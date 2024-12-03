/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.base.MoreObjects
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.base.MoreObjects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@EventName(value="confluence.http.request.stats")
@ParametersAreNonnullByDefault
public class HttpRequestStatsEvent {
    private final String requestCorrelationId;
    private final String url;
    private final Optional<String> key;
    private final long reqTime;
    private final long reqStartTime;
    private final String dbReqTimesInMicros;
    private final String dbReqFinishTimes;
    private Long requestUserTime;
    private Long requestCpuTime;
    private Long requestGCDuration;
    private Long requestGCCount;
    private String timingEventKeys;
    private String timingEventMillis;

    HttpRequestStatsEvent(String requestCorrelationId, String url, Optional<String> key, long reqTime, long reqStartTime, String dbReqTimesInMicros, String dbReqFinishTimes, Long requestUserTime, Long requestCpuTime, Long requestGCDuration, Long requestGCCount, String timingEventKeys, String timingEventMillis) {
        this.requestCorrelationId = requestCorrelationId;
        this.url = url;
        this.key = key;
        this.reqTime = reqTime;
        this.reqStartTime = reqStartTime;
        this.dbReqTimesInMicros = dbReqTimesInMicros;
        this.dbReqFinishTimes = dbReqFinishTimes;
        this.requestUserTime = requestUserTime;
        this.requestCpuTime = requestCpuTime;
        this.requestGCDuration = requestGCDuration;
        this.requestGCCount = requestGCCount;
        this.timingEventKeys = timingEventKeys;
        this.timingEventMillis = timingEventMillis;
    }

    public String getUrl() {
        return this.url;
    }

    public @Nullable String getKey() {
        return this.key.orElse(null);
    }

    public long getReqTime() {
        return this.reqTime;
    }

    public long getReqStartTime() {
        return this.reqStartTime;
    }

    public String getDbReqTimesInMicros() {
        return this.dbReqTimesInMicros;
    }

    public String getDbReqFinishTimes() {
        return this.dbReqFinishTimes;
    }

    public String getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    public Long getRequestUserTime() {
        return this.requestUserTime;
    }

    public void setRequestUserTime(Long requestUserTime) {
        this.requestUserTime = requestUserTime;
    }

    public Long getRequestCpuTime() {
        return this.requestCpuTime;
    }

    public void setRequestCpuTime(Long requestCpuTime) {
        this.requestCpuTime = requestCpuTime;
    }

    public Long getRequestGCDuration() {
        return this.requestGCDuration;
    }

    public void setRequestGCDuration(Long requestGCDuration) {
        this.requestGCDuration = requestGCDuration;
    }

    public Long getRequestGCCount() {
        return this.requestGCCount;
    }

    public void setRequestGCCount(Long requestGCCount) {
        this.requestGCCount = requestGCCount;
    }

    public String getTimingEventKeys() {
        return this.timingEventKeys;
    }

    public void setTimingEventKeys(String timingEventKeys) {
        this.timingEventKeys = timingEventKeys;
    }

    public String getTimingEventMillis() {
        return this.timingEventMillis;
    }

    public void setTimingEventMillis(String timingEventMillis) {
        this.timingEventMillis = timingEventMillis;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("requestCorrelationId", (Object)this.requestCorrelationId).add("url", (Object)this.url).add("key", this.key).add("reqTime", this.reqTime).add("reqStartTime", this.reqStartTime).add("dbReqTimesInMicros", (Object)this.dbReqTimesInMicros).add("dbReqFinishTimes", (Object)this.dbReqFinishTimes).add("requestUserTime", (Object)this.requestUserTime).add("requestCpuTime", (Object)this.requestCpuTime).add("requestGCDuration", (Object)this.requestGCDuration).add("requestGCCount", (Object)this.requestGCCount).add("timingEventKeys", (Object)this.timingEventKeys).add("timingEventMillis", (Object)this.timingEventMillis).toString();
    }
}

