/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.annotation.Nonnull;

public interface WebhooksConfiguration {
    public static final WebhooksConfiguration DEFAULT = new WebhooksConfiguration(){};

    default public double getBackoffExponent() {
        return 1.2;
    }

    @Nonnull
    default public Duration getBackoffInitialDelay() {
        return Duration.of(10L, ChronoUnit.SECONDS);
    }

    @Nonnull
    default public Duration getBackoffMaxDelay() {
        return Duration.of(2L, ChronoUnit.HOURS);
    }

    default public int getBackoffTriggerCount() {
        return 5;
    }

    @Nonnull
    default public List<String> getBlacklistedAddresses() {
        return ImmutableList.of();
    }

    @Nonnull
    default public Duration getConnectionTimeout() {
        return Duration.ofSeconds(20L);
    }

    default public int getDispatchQueueSize() {
        return 250;
    }

    @Nonnull
    default public Duration getDispatchTimeout() {
        return Duration.ofMillis(250L);
    }

    default public int getIoThreadCount() {
        return 3;
    }

    @Nonnull
    default public String getJmxDomain() {
        return "com.atlassian.webhooks";
    }

    default public int getMaxCallbackThreads() {
        return 10;
    }

    default public int getMaxHttpConnections() {
        return 200;
    }

    default public int getMaxHttpConnectionsPerHost() {
        return 5;
    }

    default public int getMaxInFlightDispatches() {
        return 500;
    }

    default public long getMaxResponseBodySize() {
        return 16384L;
    }

    @Nonnull
    default public Duration getSocketTimeout() {
        return Duration.ofSeconds(20L);
    }

    @Nonnull
    default public Duration getStatisticsFlushInterval() {
        return Duration.ofSeconds(30L);
    }

    default public boolean isStatisticsEnabled() {
        return true;
    }

    default public boolean isInvocationHistoryEnabled() {
        return false;
    }
}

