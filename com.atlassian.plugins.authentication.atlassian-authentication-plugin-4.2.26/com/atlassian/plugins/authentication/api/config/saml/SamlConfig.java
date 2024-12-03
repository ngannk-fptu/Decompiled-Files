/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.api.config.saml;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class SamlConfig
extends AbstractIdpConfig {
    private static final String CROWD_SAML_URL_SUFFIX = "/console/secure/saml/sso.action";
    private final IdpType idpType;
    private final String ssoUrl;
    private final String certificate;
    private final String usernameAttribute;

    private SamlConfig(Long id, String name, boolean enabled, boolean includeCustomerLogins, boolean enableRememberMe, @Nullable ZonedDateTime lastUpdated, @Nonnull String buttonText, @Nonnull IdpType idpType, @Nonnull String ssoUrl, @Nonnull String issuer, @Nonnull String certificate, @Nullable String usernameAttribute, JustInTimeConfig justInTimeConfig) {
        super(id, name, enabled, issuer, includeCustomerLogins, enableRememberMe, lastUpdated, buttonText, justInTimeConfig);
        this.idpType = idpType;
        this.ssoUrl = ssoUrl;
        this.certificate = certificate;
        this.usernameAttribute = usernameAttribute;
    }

    @Override
    @Nonnull
    public SsoType getSsoType() {
        return SsoType.SAML;
    }

    @Nonnull
    public IdpType getIdpType() {
        return Optional.ofNullable(this.idpType).orElseGet(this::getInferredIdpType);
    }

    @Nonnull
    public IdpType getInferredIdpType() {
        if (StringUtils.endsWith((CharSequence)this.ssoUrl, (CharSequence)CROWD_SAML_URL_SUFFIX)) {
            return IdpType.CROWD;
        }
        return IdpType.GENERIC;
    }

    public String getSsoUrl() {
        return this.ssoUrl;
    }

    public String getCertificate() {
        return this.certificate;
    }

    @Nullable
    public String getUsernameAttribute() {
        return this.usernameAttribute;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SamlConfig that = (SamlConfig)o;
        return this.idpType == that.idpType && Objects.equals(this.ssoUrl, that.ssoUrl) && Objects.equals(this.certificate, that.certificate) && Objects.equals(this.usernameAttribute, that.usernameAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode(), this.idpType, this.ssoUrl, this.certificate, this.usernameAttribute});
    }

    public static Optional<SamlConfig> from(IdpConfig config) {
        return config instanceof SamlConfig ? Optional.of((SamlConfig)config) : Optional.empty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull SamlConfig samlConfig) {
        return new Builder(samlConfig);
    }

    public static class Builder
    extends AbstractIdpConfig.Builder<Builder> {
        private IdpType idpType = IdpType.GENERIC;
        private String ssoUrl;
        private String certificate;
        private String usernameAttribute;

        private Builder() {
        }

        private Builder(@Nonnull SamlConfig samlConfig) {
            super(samlConfig);
            this.idpType = samlConfig.getIdpType();
            this.ssoUrl = samlConfig.getSsoUrl();
            this.certificate = samlConfig.getCertificate();
            this.usernameAttribute = samlConfig.getUsernameAttribute();
            this.justInTimeConfig = samlConfig.getJustInTimeConfig();
        }

        public Builder setIdpType(IdpType idpType) {
            this.idpType = idpType;
            return this;
        }

        public Builder setSsoUrl(String ssoUrl) {
            this.ssoUrl = ssoUrl;
            return this;
        }

        public Builder setCertificate(String certificate) {
            this.certificate = certificate;
            return this;
        }

        public Builder setUsernameAttribute(String usernameAttribute) {
            this.usernameAttribute = usernameAttribute;
            return this;
        }

        public Builder setCrowdBaseUrl(@Nonnull String crowdBaseUrl) {
            String strippedCrowdBaseUrl = StringUtils.removeEnd((String)crowdBaseUrl, (String)SamlConfig.CROWD_SAML_URL_SUFFIX);
            return (Builder)this.setIdpType(IdpType.CROWD).setSsoUrl(StringUtils.stripEnd((String)strippedCrowdBaseUrl, (String)"/") + SamlConfig.CROWD_SAML_URL_SUFFIX).setIssuer(strippedCrowdBaseUrl);
        }

        @Override
        @Nonnull
        public SamlConfig build() {
            return new SamlConfig(this.id, this.name, this.enabled, this.includeCustomerLogins, this.enableRememberMe, this.lastUpdated, this.buttonText, this.idpType, this.ssoUrl, this.issuer, this.certificate, this.usernameAttribute, this.justInTimeConfig);
        }
    }

    public static enum IdpType {
        GENERIC,
        CROWD;


        @Nonnull
        public static Optional<IdpType> fromName(@Nullable String value) {
            return Arrays.stream(IdpType.values()).filter(type -> Objects.equals(type.name(), value)).findFirst();
        }
    }
}

