/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.SsoConfig;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ImmutableSsoConfig
implements SsoConfig {
    private final boolean showLoginForm;
    private final boolean enableAuthenticationFallback;
    private final boolean showLoginFormForJsm;
    private final String discoveryRefreshCron;
    private final ZonedDateTime lastUpdated;

    public ImmutableSsoConfig(boolean showLoginForm, boolean enableAuthenticationFallback, boolean showLoginFormForJsm, String discoveryRefreshCron, ZonedDateTime lastUpdated) {
        this.showLoginForm = showLoginForm;
        this.enableAuthenticationFallback = enableAuthenticationFallback;
        this.showLoginFormForJsm = showLoginFormForJsm;
        this.discoveryRefreshCron = discoveryRefreshCron;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean getShowLoginForm() {
        return this.showLoginForm;
    }

    @Override
    public boolean getShowLoginFormForJsm() {
        return this.showLoginFormForJsm;
    }

    @Override
    public boolean enableAuthenticationFallback() {
        return this.enableAuthenticationFallback;
    }

    @Override
    public String getDiscoveryRefreshCron() {
        return this.discoveryRefreshCron;
    }

    @Override
    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableSsoConfig that = (ImmutableSsoConfig)o;
        return this.showLoginForm == that.showLoginForm && this.enableAuthenticationFallback == that.enableAuthenticationFallback && this.showLoginFormForJsm == that.showLoginFormForJsm && Objects.equals(this.discoveryRefreshCron, that.discoveryRefreshCron) && Objects.equals(this.lastUpdated, that.lastUpdated);
    }

    public int hashCode() {
        return Objects.hash(this.showLoginForm, this.enableAuthenticationFallback, this.showLoginFormForJsm, this.discoveryRefreshCron, this.lastUpdated);
    }

    public Builder toBuilder() {
        return ImmutableSsoConfig.toBuilder(this);
    }

    public static Builder toBuilder(SsoConfig ssoConfig) {
        return new Builder(ssoConfig);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected boolean showLoginForm;
        protected boolean enableAuthenticationFallback;
        protected boolean showLoginFormForJsm;
        protected String discoveryRefreshCron;
        protected ZonedDateTime lastUpdated;

        protected Builder() {
        }

        protected Builder(@Nonnull SsoConfig ssoConfig) {
            this.showLoginForm = ssoConfig.getShowLoginForm();
            this.enableAuthenticationFallback = ssoConfig.enableAuthenticationFallback();
            this.discoveryRefreshCron = ssoConfig.getDiscoveryRefreshCron();
            this.lastUpdated = ssoConfig.getLastUpdated();
            this.showLoginFormForJsm = ssoConfig.getShowLoginFormForJsm();
        }

        public Builder setShowLoginForm(boolean showLoginForm) {
            this.showLoginForm = showLoginForm;
            return this;
        }

        public Builder setEnableAuthenticationFallback(boolean enableAuthenticationFallback) {
            this.enableAuthenticationFallback = enableAuthenticationFallback;
            return this;
        }

        public Builder setShowLoginFormForJsm(boolean showLoginFormForJsm) {
            this.showLoginFormForJsm = showLoginFormForJsm;
            return this;
        }

        public Builder setDiscoveryRefreshCron(String discoveryRefreshCron) {
            this.discoveryRefreshCron = discoveryRefreshCron;
            return this;
        }

        public Builder setLastUpdated(ZonedDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        @Nonnull
        public ImmutableSsoConfig build() {
            return new ImmutableSsoConfig(this.showLoginForm, this.enableAuthenticationFallback, this.showLoginFormForJsm, this.discoveryRefreshCron, this.lastUpdated);
        }
    }
}

