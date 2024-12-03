/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.internal.status;

import com.atlassian.applinks.internal.status.LegacyConfig;

public final class DefaultLegacyConfig
implements LegacyConfig {
    private final boolean trustedConfigured;
    private final boolean basicConfigured;

    public DefaultLegacyConfig() {
        this.trustedConfigured = false;
        this.basicConfigured = false;
    }

    private DefaultLegacyConfig(boolean trusted, boolean basic) {
        this.trustedConfigured = trusted;
        this.basicConfigured = basic;
    }

    public DefaultLegacyConfig trusted(boolean trusted) {
        return new DefaultLegacyConfig(trusted, this.basicConfigured);
    }

    public DefaultLegacyConfig basic(boolean basic) {
        return new DefaultLegacyConfig(this.trustedConfigured, basic);
    }

    @Override
    public boolean isTrustedConfigured() {
        return this.trustedConfigured;
    }

    @Override
    public boolean isBasicConfigured() {
        return this.basicConfigured;
    }
}

