/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.ImmutableJustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.google.common.base.Preconditions;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractIdpConfig
implements IdpConfig {
    private final Long id;
    private final String name;
    private final boolean enabled;
    private final String issuer;
    private final boolean includeCustomerLogins;
    private final boolean enableRememberMe;
    private final ZonedDateTime lastUpdated;
    private final String buttonText;
    private final JustInTimeConfig justInTimeConfig;

    public AbstractIdpConfig(Long id, String name, boolean enabled, String issuer, boolean includeCustomerLogins, boolean enableRememberMe, ZonedDateTime lastUpdated, @Nonnull String buttonText, @Nonnull JustInTimeConfig justInTimeConfig) {
        Preconditions.checkNotNull((Object)justInTimeConfig);
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.issuer = issuer;
        this.includeCustomerLogins = includeCustomerLogins;
        this.enableRememberMe = enableRememberMe;
        this.lastUpdated = lastUpdated;
        this.buttonText = buttonText;
        this.justInTimeConfig = justInTimeConfig;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    @Override
    public boolean isIncludeCustomerLogins() {
        return this.includeCustomerLogins;
    }

    @Override
    public boolean isEnableRememberMe() {
        return this.enableRememberMe;
    }

    @Override
    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    @Override
    public String getButtonText() {
        return this.buttonText;
    }

    @Override
    public JustInTimeConfig getJustInTimeConfig() {
        return this.justInTimeConfig;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractIdpConfig idpConfig = (AbstractIdpConfig)o;
        return this.includeCustomerLogins == idpConfig.includeCustomerLogins && this.enableRememberMe == idpConfig.enableRememberMe && this.enabled == idpConfig.enabled && Objects.equals(this.issuer, idpConfig.issuer) && Objects.equals(this.id, idpConfig.id) && Objects.equals(this.name, idpConfig.name) && Objects.equals(this.lastUpdated, idpConfig.lastUpdated) && Objects.equals(this.buttonText, idpConfig.buttonText) && Objects.equals(this.justInTimeConfig, idpConfig.justInTimeConfig);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.enabled, this.issuer, this.includeCustomerLogins, this.enableRememberMe, this.lastUpdated, this.buttonText, this.justInTimeConfig);
    }

    public abstract Builder toBuilder();

    public static abstract class Builder<T extends Builder<T>> {
        protected Long id;
        protected String name;
        protected boolean enabled;
        protected String issuer;
        protected boolean includeCustomerLogins;
        protected boolean enableRememberMe = true;
        protected ZonedDateTime lastUpdated;
        protected String buttonText;
        protected JustInTimeConfig justInTimeConfig = ImmutableJustInTimeConfig.builder().setEnabled(false).build();

        protected Builder() {
        }

        protected Builder(@Nonnull AbstractIdpConfig idpConfig) {
            this.id = idpConfig.getId();
            this.name = idpConfig.getName();
            this.enabled = idpConfig.isEnabled();
            this.issuer = idpConfig.getIssuer();
            this.includeCustomerLogins = idpConfig.isIncludeCustomerLogins();
            this.enableRememberMe = idpConfig.isEnableRememberMe();
            this.lastUpdated = idpConfig.getLastUpdated();
            this.buttonText = idpConfig.getButtonText();
            this.justInTimeConfig = idpConfig.getJustInTimeConfig();
        }

        public T setId(Long id) {
            this.id = id;
            return (T)this;
        }

        public T setName(String name) {
            this.name = name;
            return (T)this;
        }

        public T setEnabled(boolean enabled) {
            this.enabled = enabled;
            return (T)this;
        }

        public T setIssuer(String issuer) {
            this.issuer = issuer;
            return (T)this;
        }

        public T setIncludeCustomerLogins(boolean includeCustomerLogins) {
            this.includeCustomerLogins = includeCustomerLogins;
            return (T)this;
        }

        public T setEnableRememberMe(boolean enableRememberMe) {
            this.enableRememberMe = enableRememberMe;
            return (T)this;
        }

        public T setLastUpdated(ZonedDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return (T)this;
        }

        public T setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return (T)this;
        }

        public T setJustInTimeConfig(JustInTimeConfig justInTimeConfig) {
            this.justInTimeConfig = justInTimeConfig;
            return (T)this;
        }

        @Nonnull
        public abstract AbstractIdpConfig build();
    }
}

