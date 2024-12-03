/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

public class ConfigError {
    private final String error;

    public ConfigError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    public String toString() {
        return "CacheManager configuration: " + this.getError();
    }
}

