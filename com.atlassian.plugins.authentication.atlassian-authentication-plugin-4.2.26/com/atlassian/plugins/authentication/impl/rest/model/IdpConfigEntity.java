/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.rest.model.ISO8601DateDeserializer;
import com.atlassian.plugins.authentication.impl.rest.model.ISO8601DateSerializer;
import com.atlassian.plugins.authentication.impl.rest.model.JitConfigEntity;
import com.google.common.collect.ImmutableList;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class IdpConfigEntity {
    @JsonProperty(value="id")
    private Long id;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="sso-type")
    private SsoType ssoType;
    @JsonProperty(value="enabled")
    private Boolean enabled;
    @JsonProperty(value="include-customer-logins")
    private Boolean includeCustomerLogins;
    @JsonProperty(value="enable-remember-me")
    private Boolean enableRememberMe;
    @JsonProperty(value="last-updated")
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    @JsonSerialize(using=ISO8601DateSerializer.class)
    private ZonedDateTime lastUpdated;
    @JsonProperty(value="jit-configuration")
    private JitConfigEntity jitConfiguration;
    @JsonProperty(value="button-text")
    private String buttonText;
    @JsonProperty(value="idp-type")
    private SamlConfig.IdpType idpType;
    @JsonProperty(value="sso-url")
    private String ssoUrl;
    @JsonProperty(value="sso-issuer")
    private String ssoIssuer;
    @JsonProperty(value="crowd-url")
    private String crowdUrl;
    @JsonProperty(value="certificate")
    private String certificate;
    @JsonProperty(value="username-attribute")
    private String userAttribute;
    @JsonProperty(value="issuer-url")
    private String issuerUrl;
    @JsonProperty(value="client-id")
    private String clientId;
    @JsonProperty(value="client-secret")
    private String clientSecret;
    @JsonProperty(value="authorization-endpoint")
    private String authorizationEndpoint;
    @JsonProperty(value="token-endpoint")
    private String tokenEndpoint;
    @JsonProperty(value="userinfo-endpoint")
    private String userInfoEndpoint;
    @JsonProperty(value="additional-scopes")
    private List<String> additionalScopes;
    @JsonProperty(value="username-claim")
    private String usernameClaim;
    @JsonProperty(value="discovery-enabled")
    private Boolean discoveryEnabled;

    public IdpConfigEntity() {
    }

    public IdpConfigEntity(IdpConfig config) {
        this.id = config.getId();
        this.name = config.getName();
        this.ssoType = config.getSsoType();
        this.enabled = config.isEnabled();
        this.includeCustomerLogins = config.isIncludeCustomerLogins();
        this.enableRememberMe = config.isEnableRememberMe();
        this.lastUpdated = config.getLastUpdated();
        this.buttonText = config.getButtonText();
        if (config.getSsoType() == SsoType.SAML) {
            SamlConfig samlConfig = (SamlConfig)config;
            this.idpType = samlConfig.getIdpType();
            if (this.idpType == SamlConfig.IdpType.CROWD) {
                this.crowdUrl = samlConfig.getIssuer();
            } else {
                this.ssoUrl = samlConfig.getSsoUrl();
                this.ssoIssuer = samlConfig.getIssuer();
            }
            this.certificate = samlConfig.getCertificate();
            this.userAttribute = samlConfig.getUsernameAttribute();
        } else if (config.getSsoType() == SsoType.OIDC) {
            OidcConfig oidcConfig = (OidcConfig)config;
            this.issuerUrl = oidcConfig.getIssuer();
            this.clientId = oidcConfig.getClientId();
            if (!oidcConfig.isDiscoveryEnabled()) {
                this.authorizationEndpoint = oidcConfig.getAuthorizationEndpoint();
                this.tokenEndpoint = oidcConfig.getTokenEndpoint();
                this.userInfoEndpoint = oidcConfig.getUserInfoEndpoint();
            }
            this.discoveryEnabled = oidcConfig.isDiscoveryEnabled();
            this.additionalScopes = oidcConfig.getAdditionalScopes();
            this.usernameClaim = oidcConfig.getUsernameClaim();
            this.discoveryEnabled = oidcConfig.isDiscoveryEnabled();
        }
        JustInTimeConfig justInTimeConfig = config.getJustInTimeConfig();
        if (justInTimeConfig != null) {
            this.jitConfiguration = new JitConfigEntity(justInTimeConfig);
        }
    }

    protected IdpConfigEntity(Long id, String name, SsoType ssoType, Boolean enabled, Boolean includeCustomerLogins, Boolean enableRememberMe, ZonedDateTime lastUpdated, JitConfigEntity jitConfiguration, String buttonText, SamlConfig.IdpType idpType, String ssoUrl, String ssoIssuer, String crowdUrl, String certificate, String userAttribute, String issuerUrl, String clientId, String clientSecret, String authorizationEndpoint, String tokenEndpoint, String userInfoEndpoint, List<String> additionalScopes, String usernameClaim, Boolean discoveryEnabled) {
        this.id = id;
        this.name = name;
        this.ssoType = ssoType;
        this.enabled = enabled;
        this.includeCustomerLogins = includeCustomerLogins;
        this.enableRememberMe = enableRememberMe;
        this.lastUpdated = lastUpdated;
        this.jitConfiguration = jitConfiguration;
        this.buttonText = buttonText;
        this.idpType = idpType;
        this.ssoUrl = ssoUrl;
        this.ssoIssuer = ssoIssuer;
        this.crowdUrl = crowdUrl;
        this.certificate = certificate;
        this.userAttribute = userAttribute;
        this.issuerUrl = issuerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;
        this.additionalScopes = additionalScopes != null ? ImmutableList.copyOf(additionalScopes) : null;
        this.usernameClaim = usernameClaim;
        this.discoveryEnabled = discoveryEnabled;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public SsoType getSsoType() {
        return this.ssoType;
    }

    public String getCertificate() {
        return this.certificate;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Boolean getIncludeCustomerLogins() {
        return this.includeCustomerLogins;
    }

    public Boolean getEnableRememberMe() {
        return this.enableRememberMe;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public SamlConfig.IdpType getIdpType() {
        return this.idpType;
    }

    public String getSsoUrl() {
        return this.ssoUrl;
    }

    public String getSsoIssuer() {
        return this.ssoIssuer;
    }

    public String getCrowdUrl() {
        return this.crowdUrl;
    }

    public String getUserAttribute() {
        return this.userAttribute;
    }

    public String getIssuerUrl() {
        return this.issuerUrl;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    public String getUserInfoEndpoint() {
        return this.userInfoEndpoint;
    }

    public Boolean getDiscoveryEnabled() {
        return this.discoveryEnabled;
    }

    @Nullable
    public List<String> getAdditionalScopes() {
        return this.additionalScopes;
    }

    public String getUsernameClaim() {
        return this.usernameClaim;
    }

    @VisibleForTesting
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @VisibleForTesting
    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    @VisibleForTesting
    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    @VisibleForTesting
    public void setUserInfoEndpoint(String userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public JitConfigEntity getJitConfiguration() {
        return this.jitConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdpConfigEntity that = (IdpConfigEntity)o;
        return Objects.equals(this.id, that.id) && this.ssoType == that.ssoType && Objects.equals(this.name, that.name) && Objects.equals(this.enabled, that.enabled) && Objects.equals(this.includeCustomerLogins, that.includeCustomerLogins) && Objects.equals(this.enableRememberMe, that.enableRememberMe) && Objects.equals(this.lastUpdated, that.lastUpdated) && Objects.equals(this.buttonText, that.buttonText) && this.idpType == that.idpType && Objects.equals(this.ssoUrl, that.ssoUrl) && Objects.equals(this.ssoIssuer, that.ssoIssuer) && Objects.equals(this.crowdUrl, that.crowdUrl) && Objects.equals(this.certificate, that.certificate) && Objects.equals(this.userAttribute, that.userAttribute) && Objects.equals(this.issuerUrl, that.issuerUrl) && Objects.equals(this.clientId, that.clientId) && Objects.equals(this.clientSecret, that.clientSecret) && Objects.equals(this.authorizationEndpoint, that.authorizationEndpoint) && Objects.equals(this.tokenEndpoint, that.tokenEndpoint) && Objects.equals(this.userInfoEndpoint, that.userInfoEndpoint) && Objects.equals(this.additionalScopes, that.additionalScopes) && Objects.equals(this.usernameClaim, that.usernameClaim) && Objects.equals(this.jitConfiguration, that.jitConfiguration) && Objects.equals(this.discoveryEnabled, that.discoveryEnabled);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.ssoType, this.enabled, this.includeCustomerLogins, this.enableRememberMe, this.lastUpdated, this.buttonText, this.idpType, this.ssoUrl, this.ssoIssuer, this.crowdUrl, this.certificate, this.userAttribute, this.issuerUrl, this.clientId, this.clientSecret, this.authorizationEndpoint, this.tokenEndpoint, this.userInfoEndpoint, this.additionalScopes, this.usernameClaim, this.jitConfiguration, this.discoveryEnabled});
    }

    public String toString() {
        return "IdpConfigEntity{id=" + this.id + ", name=" + this.name + ", ssoType=" + (Object)((Object)this.ssoType) + ", enabled=" + this.enabled + ", includeCustomerLogins=" + this.includeCustomerLogins + ", enableRememberMe=" + this.enableRememberMe + ", lastUpdated=" + this.lastUpdated + ", buttonText=" + this.buttonText + ", idpType=" + (Object)((Object)this.idpType) + ", ssoUrl='" + this.ssoUrl + '\'' + ", ssoIssuer='" + this.ssoIssuer + '\'' + ", crowdUrl='" + this.crowdUrl + '\'' + ", certificate='" + this.certificate + '\'' + ", userAttribute='" + this.userAttribute + '\'' + ", issuerUrl='" + this.issuerUrl + '\'' + ", clientId='" + this.clientId + '\'' + ", clientSecret='" + this.clientSecret + '\'' + ", authorizationEndpoint='" + this.authorizationEndpoint + '\'' + ", tokenEndpoint='" + this.tokenEndpoint + '\'' + ", userInfoEndpoint='" + this.userInfoEndpoint + '\'' + ", additionalScopes=" + this.additionalScopes + ", usernameClaim='" + this.usernameClaim + '\'' + ", jitConfiguration=" + this.jitConfiguration + ", discoveryEnabled=" + this.discoveryEnabled + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(IdpConfigEntity data) {
        return new Builder(data);
    }

    public static final class Builder {
        private Long id;
        private String name;
        private SsoType ssoType;
        private Boolean enabled;
        private Boolean includeCustomerLogins;
        private Boolean enableRememberMe;
        private ZonedDateTime lastUpdated;
        private JitConfigEntity jitConfiguration;
        private String buttonText;
        private SamlConfig.IdpType idpType;
        private String ssoUrl;
        private String ssoIssuer;
        private String crowdUrl;
        private String certificate;
        private String userAttribute;
        private String issuerUrl;
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private String userInfoEndpoint;
        private List<String> additionalScopes;
        private String usernameClaim;
        private Boolean discoveryEnabled;

        private Builder() {
        }

        private Builder(IdpConfigEntity initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName();
            this.ssoType = initialData.getSsoType();
            this.enabled = initialData.getEnabled();
            this.includeCustomerLogins = initialData.getIncludeCustomerLogins();
            this.enableRememberMe = initialData.getEnableRememberMe();
            this.lastUpdated = initialData.getLastUpdated();
            this.jitConfiguration = initialData.getJitConfiguration();
            this.buttonText = initialData.getButtonText();
            this.idpType = initialData.getIdpType();
            this.ssoUrl = initialData.getSsoUrl();
            this.ssoIssuer = initialData.getSsoIssuer();
            this.crowdUrl = initialData.getCrowdUrl();
            this.certificate = initialData.getCertificate();
            this.userAttribute = initialData.getUserAttribute();
            this.issuerUrl = initialData.getIssuerUrl();
            this.clientId = initialData.getClientId();
            this.clientSecret = initialData.getClientSecret();
            this.authorizationEndpoint = initialData.getAuthorizationEndpoint();
            this.tokenEndpoint = initialData.getTokenEndpoint();
            this.userInfoEndpoint = initialData.getUserInfoEndpoint();
            this.additionalScopes = initialData.getAdditionalScopes() != null ? ImmutableList.copyOf(initialData.getAdditionalScopes()) : null;
            this.usernameClaim = initialData.getUsernameClaim();
            this.discoveryEnabled = initialData.getDiscoveryEnabled();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSsoType(SsoType ssoType) {
            this.ssoType = ssoType;
            return this;
        }

        public Builder setEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setIncludeCustomerLogins(Boolean includeCustomerLogins) {
            this.includeCustomerLogins = includeCustomerLogins;
            return this;
        }

        public Builder setEnableRememberMe(Boolean enableRememberMe) {
            this.enableRememberMe = enableRememberMe;
            return this;
        }

        public Builder setLastUpdated(ZonedDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder setJitConfiguration(JitConfigEntity jitConfiguration) {
            this.jitConfiguration = jitConfiguration;
            return this;
        }

        public Builder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public Builder setIdpType(SamlConfig.IdpType idpType) {
            this.idpType = idpType;
            return this;
        }

        public Builder setSsoUrl(String ssoUrl) {
            this.ssoUrl = ssoUrl;
            return this;
        }

        public Builder setSsoIssuer(String ssoIssuer) {
            this.ssoIssuer = ssoIssuer;
            return this;
        }

        public Builder setCrowdUrl(String crowdUrl) {
            this.crowdUrl = crowdUrl;
            return this;
        }

        public Builder setCertificate(String certificate) {
            this.certificate = certificate;
            return this;
        }

        public Builder setUserAttribute(String userAttribute) {
            this.userAttribute = userAttribute;
            return this;
        }

        public Builder setIssuerUrl(String issuerUrl) {
            this.issuerUrl = issuerUrl;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setAuthorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public Builder setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public Builder setUserInfoEndpoint(String userInfoEndpoint) {
            this.userInfoEndpoint = userInfoEndpoint;
            return this;
        }

        public Builder setAdditionalScopes(List<String> additionalScopes) {
            this.additionalScopes = additionalScopes;
            return this;
        }

        public Builder addAdditionalScope(String additionalScope) {
            this.additionalScopes.add(additionalScope);
            return this;
        }

        public Builder addAdditionalScopes(Iterable<String> additionalScopes) {
            for (String additionalScope : additionalScopes) {
                this.addAdditionalScope(additionalScope);
            }
            return this;
        }

        public Builder setUsernameClaim(String usernameClaim) {
            this.usernameClaim = usernameClaim;
            return this;
        }

        public Builder setDiscoveryEnabled(Boolean discoveryEnabled) {
            this.discoveryEnabled = discoveryEnabled;
            return this;
        }

        public IdpConfigEntity build() {
            return new IdpConfigEntity(this.id, this.name, this.ssoType, this.enabled, this.includeCustomerLogins, this.enableRememberMe, this.lastUpdated, this.jitConfiguration, this.buttonText, this.idpType, this.ssoUrl, this.ssoIssuer, this.crowdUrl, this.certificate, this.userAttribute, this.issuerUrl, this.clientId, this.clientSecret, this.authorizationEndpoint, this.tokenEndpoint, this.userInfoEndpoint, this.additionalScopes, this.usernameClaim, this.discoveryEnabled);
        }
    }

    public static interface Config {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String SSO_TYPE = "sso-type";
        public static final String ENABLED = "enabled";
        public static final String INCLUDE_CUSTOMER_LOGINS = "include-customer-logins";
        public static final String ENABLE_REMEMBER_ME = "enable-remember-me";
        public static final String JIT_CONFIGURATION = "jit-configuration";
        public static final String BUTTON_TEXT = "button-text";

        public static interface Oidc {
            public static final String ISSUER_URL = "issuer-url";
            public static final String CLIENT_ID = "client-id";
            public static final String CLIENT_SECRET = "client-secret";
            public static final String AUTHORIZATION_ENDPOINT = "authorization-endpoint";
            public static final String TOKEN_ENDPOINT = "token-endpoint";
            public static final String USER_INFO_ENDPOINT = "userinfo-endpoint";
            public static final String ADDITIONAL_SCOPES = "additional-scopes";
            public static final String USERNAME_CLAIM = "username-claim";
            public static final String DISCOVERY_ENABLED = "discovery-enabled";
        }

        public static interface Saml {
            public static final String IDP_TYPE = "idp-type";
            public static final String SSO_URL = "sso-url";
            public static final String SSO_ISSUER = "sso-issuer";
            public static final String CROWD_URL = "crowd-url";
            public static final String CERTIFICATE = "certificate";
            public static final String USERNAME_ATTRIBUTE = "username-attribute";
        }
    }
}

