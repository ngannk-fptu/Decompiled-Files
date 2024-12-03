/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.authentication.api.config.AbstractIdpConfig
 *  com.atlassian.plugins.authentication.api.config.IdpConfig
 *  com.atlassian.plugins.authentication.api.config.IdpConfigService
 *  com.atlassian.plugins.authentication.api.config.JustInTimeConfig
 *  com.atlassian.plugins.authentication.api.config.SsoConfig
 *  com.atlassian.plugins.authentication.api.config.SsoConfigService
 *  com.atlassian.plugins.authentication.api.config.oidc.OidcConfig
 *  com.atlassian.plugins.authentication.api.config.saml.SamlConfig
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.util.List;

public class SsoDataFetcher {
    private final IdpConfigService idpConfigService;
    private final SsoConfigService ssoConfigService;

    public SsoDataFetcher(IdpConfigService idpConfigService, SsoConfigService ssoConfigService) {
        this.idpConfigService = idpConfigService;
        this.ssoConfigService = ssoConfigService;
    }

    private List<IdpConfig> getIdpConfigData() {
        return this.idpConfigService.getIdpConfigs();
    }

    private SsoConfig getSsoConfigData() {
        return this.ssoConfigService.getSsoConfig();
    }

    public void addSpecificConfigData(SupportDataBuilder categorizedBuilder) {
        for (IdpConfig config : this.getIdpConfigData()) {
            AbstractIdpConfig abstractConfig = (AbstractIdpConfig)config;
            SupportDataBuilder configBuilder = categorizedBuilder.addCategory("stp.properties.sso.configuration");
            this.addCommonConfigData(configBuilder, abstractConfig);
            if (config instanceof SamlConfig) {
                this.addSamlConfigData(configBuilder, abstractConfig);
                continue;
            }
            if (!(config instanceof OidcConfig)) continue;
            this.addOpenIdConfigData(configBuilder, abstractConfig);
        }
    }

    private void addCommonConfigData(SupportDataBuilder configBuilder, AbstractIdpConfig config) {
        configBuilder.addValue("stp.properties.sso.configuration.name", config.getName());
        configBuilder.addValue("stp.properties.sso.configuration.enabled", String.valueOf(config.isEnabled()));
        configBuilder.addValue("stp.properties.sso.configuration.type", config.getSsoType().toString());
        configBuilder.addValue("stp.properties.sso.settings.issuer", config.getIssuer());
        configBuilder.addValue("stp.properties.sso.behaviour.remember.user.logins", String.valueOf(config.isEnableRememberMe()));
        configBuilder.addValue("stp.properties.sso.configuration.customer.logins", String.valueOf(config.isIncludeCustomerLogins()));
        configBuilder.addValue("stp.properties.sso.configuration.button.text", config.getButtonText());
        configBuilder.addValue("stp.properties.sso.configuration.last.updated", String.valueOf(config.getLastUpdated()));
        SupportDataBuilder jitProvisioning = configBuilder.addCategory("stp.properties.sso.jit.provisioning");
        JustInTimeConfig justInTimeConfig = config.getJustInTimeConfig();
        boolean justInTimeIsEnabled = justInTimeConfig.isEnabled().orElse(false);
        jitProvisioning.addValue("stp.properties.sso.jit.provisioning.enabled", String.valueOf(justInTimeIsEnabled));
        if (justInTimeIsEnabled) {
            justInTimeConfig.getDisplayNameMappingExpression().ifPresent(value -> jitProvisioning.addValue("stp.properties.sso.jit.provisioning.display.name", (String)value));
            justInTimeConfig.getEmailMappingExpression().ifPresent(value -> jitProvisioning.addValue("stp.properties.sso.jit.provisioning.email", (String)value));
            justInTimeConfig.getGroupsMappingSource().ifPresent(value -> jitProvisioning.addValue("stp.properties.sso.jit.provisioning.groups", (String)value));
        }
    }

    public void addGenericConfigData(SupportDataBuilder configBuilder) {
        configBuilder.addValue("stp.properties.sso.generic.show.login.form", String.valueOf(this.getSsoConfigData().getShowLoginForm()));
        configBuilder.addValue("stp.properties.sso.generic.show.login.form.JSM", String.valueOf(this.getSsoConfigData().getShowLoginFormForJsm()));
        configBuilder.addValue("stp.properties.sso.generic.auth.fallback", String.valueOf(this.getSsoConfigData().enableAuthenticationFallback()));
    }

    private void addSamlConfigData(SupportDataBuilder configBuilder, AbstractIdpConfig config) {
        SamlConfig samlConfig = (SamlConfig)config;
        configBuilder.addValue("stp.properties.sso.configuration.idp.type", String.valueOf(samlConfig.getIdpType()));
        configBuilder.addValue("stp.properties.sso.settings.identity.provider.url", samlConfig.getSsoUrl());
        configBuilder.addValue("stp.properties.sso.settings.username.mapping", samlConfig.getUsernameAttribute());
    }

    private void addOpenIdConfigData(SupportDataBuilder supportBuilder, AbstractIdpConfig config) {
        OidcConfig oidcConfig = (OidcConfig)config;
        supportBuilder.addValue("stp.properties.sso.settings.username.mapping", oidcConfig.getUsernameClaim());
        List scopesList = oidcConfig.getAdditionalScopes();
        if (!scopesList.isEmpty()) {
            for (String scope : scopesList) {
                SupportDataBuilder additionalScopes = supportBuilder.addCategory("stp.properties.sso.settings.additional.scopes");
                additionalScopes.addValue("stp.properties.sso.settings.additional.scopes.scope", scope);
            }
        }
        SupportDataBuilder additionalSettings = supportBuilder.addCategory("stp.properties.sso.additional.settings");
        boolean isDiscoveryEnabled = oidcConfig.isDiscoveryEnabled();
        additionalSettings.addValue("stp.properties.additional.settings.is.enabled", String.valueOf(isDiscoveryEnabled));
        if (!isDiscoveryEnabled) {
            additionalSettings.addValue("stp.properties.sso.additional.settings.authorization.endpoint", oidcConfig.getAuthorizationEndpoint());
            additionalSettings.addValue("stp.properties.sso.additional.settings.token.endpoint", oidcConfig.getTokenEndpoint());
            additionalSettings.addValue("stp.properties.sso.additional.settings.userinfo.endpoint", oidcConfig.getUserInfoEndpoint());
        }
    }
}

