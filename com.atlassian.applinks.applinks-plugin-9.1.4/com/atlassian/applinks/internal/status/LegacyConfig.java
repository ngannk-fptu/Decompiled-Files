/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.internal.status;

public interface LegacyConfig {
    public boolean isTrustedConfigured();

    public boolean isBasicConfigured();

    default public boolean hasLegacy() {
        return this.isBasicConfigured() || this.isTrustedConfigured();
    }
}

