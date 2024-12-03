/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.migration;

import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.status.LegacyConfig;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class AuthenticationConfig
implements LegacyConfig {
    private final OAuthConfig oAuthConfig;
    private final boolean basicConfigured;
    private final boolean trustedConfigured;

    public AuthenticationConfig() {
        this(OAuthConfig.createDisabledConfig(), false, false);
    }

    public AuthenticationConfig(@Nonnull OAuthConfig oAuthConfig, boolean basicConfigured, boolean trustedConfigured) {
        this.oAuthConfig = Objects.requireNonNull(oAuthConfig, "oAuthConfig");
        this.basicConfigured = basicConfigured;
        this.trustedConfigured = trustedConfigured;
    }

    public boolean isOAuthConfigured() {
        return this.oAuthConfig.isEnabled();
    }

    @Override
    public boolean isBasicConfigured() {
        return this.basicConfigured;
    }

    @Override
    public boolean isTrustedConfigured() {
        return this.trustedConfigured;
    }

    @Nonnull
    public OAuthConfig getOAuthConfig() {
        return this.oAuthConfig;
    }

    public AuthenticationConfig trustedConfigured(boolean trustedConfigured) {
        return new AuthenticationConfig(this.oAuthConfig, this.basicConfigured, trustedConfigured);
    }

    public AuthenticationConfig basicConfigured(boolean basicConfigured) {
        return new AuthenticationConfig(this.oAuthConfig, basicConfigured, this.trustedConfigured);
    }

    public AuthenticationConfig oauth(OAuthConfig oAuthConfig) {
        return new AuthenticationConfig(oAuthConfig, this.basicConfigured, this.trustedConfigured);
    }
}

