/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.config.ConfigError;

public class CacheConfigError
extends ConfigError {
    private final String cacheName;

    public CacheConfigError(String error, String cacheName) {
        super(error);
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    @Override
    public String toString() {
        return "Cache '" + this.cacheName + "' error: " + this.getError();
    }
}

