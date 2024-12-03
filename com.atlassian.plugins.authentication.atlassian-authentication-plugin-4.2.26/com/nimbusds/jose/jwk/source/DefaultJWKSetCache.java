/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DefaultJWKSetCache
implements JWKSetCache {
    public static final long DEFAULT_LIFESPAN_MINUTES = 15L;
    public static final long DEFAULT_REFRESH_TIME_MINUTES = 5L;
    private final long lifespan;
    private final long refreshTime;
    private final TimeUnit timeUnit;
    private long putTimestamp = -1L;
    private JWKSet jwkSet;

    public DefaultJWKSetCache() {
        this(15L, 5L, TimeUnit.MINUTES);
    }

    public DefaultJWKSetCache(long lifespan, long refreshTime, TimeUnit timeUnit) {
        this.lifespan = lifespan;
        this.refreshTime = refreshTime;
        if ((lifespan > -1L || refreshTime > -1L) && timeUnit == null) {
            throw new IllegalArgumentException("A time unit must be specified for non-negative lifespans or refresh times");
        }
        this.timeUnit = timeUnit;
    }

    @Override
    public void put(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
        this.putTimestamp = jwkSet != null ? new Date().getTime() : -1L;
    }

    @Override
    public JWKSet get() {
        if (this.isExpired()) {
            this.jwkSet = null;
        }
        return this.jwkSet;
    }

    @Override
    public boolean requiresRefresh() {
        return this.putTimestamp > -1L && this.refreshTime > -1L && new Date().getTime() > this.putTimestamp + TimeUnit.MILLISECONDS.convert(this.refreshTime, this.timeUnit);
    }

    public long getPutTimestamp() {
        return this.putTimestamp;
    }

    public boolean isExpired() {
        return this.putTimestamp > -1L && this.lifespan > -1L && new Date().getTime() > this.putTimestamp + TimeUnit.MILLISECONDS.convert(this.lifespan, this.timeUnit);
    }

    public long getLifespan(TimeUnit timeUnit) {
        if (this.lifespan < 0L) {
            return this.lifespan;
        }
        return timeUnit.convert(this.lifespan, timeUnit);
    }

    public long getRefreshTime(TimeUnit timeUnit) {
        if (this.refreshTime < 0L) {
            return this.refreshTime;
        }
        return timeUnit.convert(this.refreshTime, timeUnit);
    }
}

