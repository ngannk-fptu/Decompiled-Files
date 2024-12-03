/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Ticker
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseConnectionStateService;
import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseLatencyMeter;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class DefaultDatabaseConnectionStateService
implements DatabaseConnectionStateService {
    private static final Duration DEFAULT_CACHE_EXPIRY = Duration.ofSeconds(30L);
    private static final Object LATENCY_CACHE_KEY = new Object();
    private final LoadingCache<Object, Optional<Duration>> cache;

    public DefaultDatabaseConnectionStateService(DatabaseLatencyMeter databaseLatencyMeter) {
        this(Objects.requireNonNull(databaseLatencyMeter), Ticker.systemTicker());
    }

    DefaultDatabaseConnectionStateService(DatabaseLatencyMeter databaseLatencyMeter, Ticker ticker) {
        this.cache = CacheBuilder.newBuilder().ticker(ticker).expireAfterWrite(DEFAULT_CACHE_EXPIRY).build(CacheLoader.from(key -> databaseLatencyMeter.measure()));
    }

    @Override
    public Optional<Duration> getLatency() {
        return (Optional)this.cache.getUnchecked(LATENCY_CACHE_KEY);
    }

    @Override
    public DatabaseConnectionStateService.DatabaseConnectionState getState() {
        return this.getLatency().map(ignore -> DatabaseConnectionStateService.DatabaseConnectionState.CONNECTED).orElse(DatabaseConnectionStateService.DatabaseConnectionState.DISCONNECTED);
    }
}

