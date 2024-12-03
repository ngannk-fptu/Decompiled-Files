/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Stopwatch
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.event.events.analytics.HttpRequestStatsEvent;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringNameGenerator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import javax.servlet.http.HttpServletRequest;

@ParametersAreNonnullByDefault
public class HttpRequestStats {
    private static final ThreadLocal<HttpRequestStats> THREAD_LOCAL = new ThreadLocal();
    private final Clock clock;
    private final LongStream.Builder dbReqTimesInMicrosBuilder;
    private final LongStream.Builder dbReqFinishTimesBuilder;
    private final Stopwatch stopWatch;
    private final Instant reqStartTime;
    private final HttpServletRequest request;
    private final String requestCorrelationId;
    private final Long requestUserTimeStart;
    private final Long requestCpuTimeStart;
    private final Long requestGarbageCollectionTimeStart;
    private final Long requestGarbageCollectionCountStart;
    private final ArrayList<TimingEvent> timingEvents = new ArrayList();
    private Optional<String> key = Optional.empty();

    public static void start(HttpServletRequest request) {
        THREAD_LOCAL.set(new HttpRequestStats(request, Stopwatch.createUnstarted(), Clock.systemUTC()));
    }

    @VisibleForTesting
    static void start(HttpServletRequest request, Stopwatch stopwatch, Clock clock) {
        THREAD_LOCAL.set(new HttpRequestStats(request, stopwatch, clock));
    }

    public static void elapse(String tag) {
        Optional.ofNullable(THREAD_LOCAL.get()).ifPresent(httpRequestStats -> HttpRequestStats.addTimingEvent(tag, httpRequestStats.stopWatch.elapsed(TimeUnit.MILLISECONDS)));
    }

    public static Optional<HttpRequestStats> addTimingEvent(String tag, long millis) {
        Preconditions.checkNotNull((Object)tag);
        return Optional.ofNullable(THREAD_LOCAL.get()).map(httpRequestStats -> {
            httpRequestStats.timingEvents.add(new TimingEvent(tag, millis));
            return httpRequestStats;
        });
    }

    public static Optional<HttpRequestStatsEvent> stop() {
        HttpRequestStats httpRequestStats = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        return Optional.ofNullable(httpRequestStats).flatMap(HttpRequestStats::build);
    }

    @Deprecated
    public static void logDbRequest(Stopwatch dbReqWatch) {
        HttpRequestStats httpRequestStats = THREAD_LOCAL.get();
        if (httpRequestStats != null) {
            httpRequestStats.logDbRequestTiming(dbReqWatch.elapsed(TimeUnit.MICROSECONDS));
        }
    }

    public static void logDbRequest(Duration duration) {
        HttpRequestStats httpRequestStats = THREAD_LOCAL.get();
        if (httpRequestStats != null) {
            httpRequestStats.logDbRequestTiming(TimeUnit.NANOSECONDS.toMicros(duration.toNanos()));
        }
    }

    private void logDbRequestTiming(long elapsedTimeMicros) {
        this.dbReqTimesInMicrosBuilder.add(elapsedTimeMicros);
        this.dbReqFinishTimesBuilder.add(Duration.between(this.reqStartTime, this.clock.instant()).toMillis());
    }

    public static void setKey(String key) {
        HttpRequestStats httpRequestStats = THREAD_LOCAL.get();
        if (httpRequestStats != null) {
            httpRequestStats.key = Optional.of(key);
        }
    }

    private static long getGarbageCollectionTime() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(GarbageCollectorMXBean::getCollectionTime).reduce(0L, (a, b) -> a + b);
    }

    private static long getGarbageCollectionCount() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).reduce(0L, (a, b) -> a + b);
    }

    private static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? TimeUnit.NANOSECONDS.toMillis(bean.getCurrentThreadCpuTime()) : 0L;
    }

    private static long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? TimeUnit.NANOSECONDS.toMillis(bean.getCurrentThreadUserTime()) : 0L;
    }

    private HttpRequestStats(HttpServletRequest request, Stopwatch stopwatch, Clock clock) {
        this.clock = clock;
        this.dbReqTimesInMicrosBuilder = LongStream.builder();
        this.dbReqFinishTimesBuilder = LongStream.builder();
        this.request = request;
        this.stopWatch = stopwatch.start();
        this.reqStartTime = clock.instant();
        this.requestCorrelationId = RequestCacheThreadLocal.getRequestCorrelationId();
        this.requestCpuTimeStart = HttpRequestStats.getCpuTime();
        this.requestUserTimeStart = HttpRequestStats.getUserTime();
        this.requestGarbageCollectionCountStart = HttpRequestStats.getGarbageCollectionCount();
        this.requestGarbageCollectionTimeStart = HttpRequestStats.getGarbageCollectionTime();
    }

    private Optional<HttpRequestStatsEvent> build() {
        this.stopWatch.stop();
        String dbReqTimesInMicros = this.joinToString(this.dbReqTimesInMicrosBuilder);
        if (dbReqTimesInMicros.isEmpty() && this.timingEvents.isEmpty()) {
            return Optional.empty();
        }
        this.timingEvents.add(new TimingEvent("serverRenderEnd", this.stopWatch.elapsed(TimeUnit.MILLISECONDS)));
        String timingEventKeys = Joiner.on((char)',').join(this.timingEvents.stream().map(event -> event.key).iterator());
        String timingEventMillis = Joiner.on((char)',').join(this.timingEvents.stream().map(event -> event.millis).iterator());
        HttpRequestStatsEvent event2 = new HttpRequestStatsEvent(this.requestCorrelationId, ConfluenceMonitoringNameGenerator.generateName(this.request), this.key, this.stopWatch.elapsed(TimeUnit.MILLISECONDS), this.reqStartTime.toEpochMilli(), dbReqTimesInMicros, this.joinToString(this.dbReqFinishTimesBuilder), HttpRequestStats.getUserTime() - this.requestUserTimeStart, HttpRequestStats.getCpuTime() - this.requestCpuTimeStart, HttpRequestStats.getGarbageCollectionTime() - this.requestGarbageCollectionTimeStart, HttpRequestStats.getGarbageCollectionCount() - this.requestGarbageCollectionCountStart, timingEventKeys, timingEventMillis);
        return Optional.of(event2);
    }

    private String joinToString(LongStream.Builder builder) {
        return Joiner.on((char)',').join((Iterator)builder.build().limit(1000L).iterator());
    }

    private static class TimingEvent {
        public final String key;
        public final long millis;

        public TimingEvent(String key, long millis) {
            this.key = key;
            this.millis = millis;
        }
    }
}

