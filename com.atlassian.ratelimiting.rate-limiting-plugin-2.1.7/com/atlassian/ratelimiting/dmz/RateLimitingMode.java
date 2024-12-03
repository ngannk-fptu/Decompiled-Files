/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dmz;

public enum RateLimitingMode {
    ON(true),
    OFF(false),
    DRY_RUN(true);

    private final boolean enabled;

    private RateLimitingMode(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isDryRun() {
        return DRY_RUN.equals((Object)this);
    }
}

