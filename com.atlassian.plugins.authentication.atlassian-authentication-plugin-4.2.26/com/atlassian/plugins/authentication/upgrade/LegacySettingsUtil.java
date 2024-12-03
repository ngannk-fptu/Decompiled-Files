/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.ImmutableJustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class LegacySettingsUtil {
    public SsoType getLegacySsoType(PluginSettings settings) {
        return SsoType.valueOf(PluginSettingsUtil.getStringValue(settings, "sso-type"));
    }

    @NotNull
    protected IdpConfig mapGenericLegacyConfig(PluginSettings settings, AbstractIdpConfig.Builder<?> configBuilder, ImmutableJustInTimeConfig.Builder jitConfigBuilder) {
        ((AbstractIdpConfig.Builder)configBuilder.setIncludeCustomerLogins(PluginSettingsUtil.getBooleanValue(settings, "include-customer-logins", false))).setEnableRememberMe(PluginSettingsUtil.getBooleanValue(settings, "enable-remember-me", false));
        jitConfigBuilder.setEnabled(PluginSettingsUtil.getBooleanValue(settings, "user-provisioning-enabled", false));
        jitConfigBuilder.setDisplayNameMappingExpression(PluginSettingsUtil.getStringValue(settings, "mapping-displayname"));
        jitConfigBuilder.setEmailMappingExpression(PluginSettingsUtil.getStringValue(settings, "mapping-email"));
        jitConfigBuilder.setGroupsMappingSource(PluginSettingsUtil.getStringValue(settings, "mapping-groups"));
        configBuilder.setJustInTimeConfig(jitConfigBuilder.build());
        return configBuilder.build();
    }

    protected SamlConfig.Builder mapLegacySamlConfig(PluginSettings settings) {
        return ((SamlConfig.Builder)SamlConfig.builder().setIdpType(SamlConfig.IdpType.valueOf(PluginSettingsUtil.getStringValue(settings, "idp-type"))).setSsoUrl(PluginSettingsUtil.getStringValue(settings, "sso-url")).setIssuer(PluginSettingsUtil.getStringValue(settings, "sso-issuer"))).setCertificate(PluginSettingsUtil.getStringValue(settings, "signing-cert")).setUsernameAttribute(PluginSettingsUtil.getStringValue(settings, "username-attribute"));
    }

    protected OidcConfig.Builder mapLegacyOidcConfig(PluginSettings settings) {
        return ((OidcConfig.Builder)OidcConfig.builder().setIssuer(PluginSettingsUtil.getStringValue(settings, "issuer-url"))).setClientId(PluginSettingsUtil.getStringValue(settings, "client-id")).setClientSecret(PluginSettingsUtil.getStringValue(settings, "client-secret")).setAuthorizationEndpoint(PluginSettingsUtil.getStringValue(settings, "authorization-endpoint")).setTokenEndpoint(PluginSettingsUtil.getStringValue(settings, "token-endpoint")).setUserInfoEndpoint(PluginSettingsUtil.getStringValue(settings, "userinfo-endpoint")).setDiscoveryEnabled(PluginSettingsUtil.getBooleanValue(settings, "use-discovery", false)).setAdditionalScopes(PluginSettingsUtil.getListValue(settings, "additional-scopes", String::valueOf)).setUsernameClaim(PluginSettingsUtil.getStringValue(settings, "username-claim"));
    }

    protected SamlConfig.IdpType getIdpType(PluginSettings settings) {
        return SamlConfig.IdpType.valueOf(PluginSettingsUtil.getStringValue(settings, "idp-type"));
    }

    protected void removeLegacyConfigSettings(PluginSettings settings) {
        Stream.of("redirect-on-login", "sso-type", "user-provisioning-enabled", "enable-remember-me", "include-customer-logins", "mapping-displayname", "mapping-email", "mapping-groups", "idp-type", "sso-url", "sso-issuer", "signing-cert", "username-attribute", "issuer-url", "client-id", "client-secret", "authorization-endpoint", "token-endpoint", "userinfo-endpoint", "use-discovery", "additional-scopes", "username-claim", "additional-jit-scopes", "allow-redirect-override").forEach(config -> PluginSettingsUtil.removeValue(settings, config));
    }
}

