/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.plugins.authentication.impl.web;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class SessionDataCacheConfiguration {
    private final long cacheEntryLifetimeInSeconds;

    public SessionDataCacheConfiguration(long cacheEntryLifetimeInSeconds) {
        this.cacheEntryLifetimeInSeconds = cacheEntryLifetimeInSeconds;
    }

    public long getCacheEntryLifetimeInSeconds() {
        return this.cacheEntryLifetimeInSeconds;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SessionDataCacheConfiguration that = (SessionDataCacheConfiguration)o;
        return Objects.equals(this.getCacheEntryLifetimeInSeconds(), that.getCacheEntryLifetimeInSeconds());
    }

    public int hashCode() {
        return Objects.hash(this.getCacheEntryLifetimeInSeconds());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("cacheEntryLifetimeInSeconds", this.getCacheEntryLifetimeInSeconds()).toString();
    }
}

