/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.vcache.internal.RequestMetrics
 *  com.atlassian.vcache.internal.core.metrics.EmptyRequestMetrics
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.impl.vcache.metrics.CacheStatisticsUtils;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.core.metrics.EmptyRequestMetrics;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VCacheMetricsLogger {
    private static final Logger localLog = LoggerFactory.getLogger((String)(VCacheMetricsLogger.class.getName() + ".local"));
    private static final Logger log = LoggerFactory.getLogger(VCacheMetricsLogger.class);
    private static final boolean LOCAL_LOGGING_ENABLED = Boolean.getBoolean("confluence.vcache.metricsLogging.local.enabled");

    VCacheMetricsLogger() {
    }

    static void logMetrics(String requestURI, Stopwatch reqExecStopwatch, Supplier<RequestMetrics> metricsSupplier) {
        RequestMetrics metrics = metricsSupplier.get();
        if (!(metrics instanceof EmptyRequestMetrics)) {
            LoggingContext.executeWithContext((Map)ImmutableMap.of((Object)"requestId", (Object)RequestCacheThreadLocal.getRequestCorrelationId(), (Object)"requestExecutionTime", (Object)reqExecStopwatch.elapsed(TimeUnit.MILLISECONDS), (Object)"instrumentation", (Object)VCacheMetricsLogger.getMetricsLoggingPayload(requestURI, metrics)), () -> {
                if (LOCAL_LOGGING_ENABLED) {
                    localLog.info("VCache request metrics for {}", (Object)requestURI);
                }
                log.info("VCache request metrics for {}", (Object)requestURI);
            });
        }
    }

    private static Jsonable getMetricsLoggingPayload(String requestURI, RequestMetrics metrics) {
        return writer -> new ObjectMapper().writeValue(writer, (Object)ImmutableMap.of((Object)"path", (Object)requestURI, (Object)"data", (Object)ImmutableMap.of((Object)"cache", CacheStatisticsUtils.collectVCacheStats(metrics).collect(Collectors.toList()), (Object)"remote", CacheStatisticsUtils.remoteStats(metrics))));
    }
}

