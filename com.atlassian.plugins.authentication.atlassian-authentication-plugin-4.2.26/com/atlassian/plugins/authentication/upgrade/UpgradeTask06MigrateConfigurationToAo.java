/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.ImmutableJustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.plugins.authentication.impl.config.SsoConfigDao;
import com.atlassian.plugins.authentication.upgrade.LegacySettingsUtil;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask06MigrateConfigurationToAo
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpgradeTask06MigrateConfigurationToAo.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SsoConfigDao ssoConfigDao;
    private final LegacySettingsUtil legacySettingsUtil;
    private final ApplicationProperties applicationProperties;

    @Inject
    public UpgradeTask06MigrateConfigurationToAo(@ComponentImport PluginSettingsFactory pluginSettingsFactory, SsoConfigDao ssoConfigDao, LegacySettingsUtil legacySettingsUtil, @ComponentImport ApplicationProperties applicationProperties) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.ssoConfigDao = ssoConfigDao;
        this.legacySettingsUtil = legacySettingsUtil;
        this.applicationProperties = applicationProperties;
    }

    public int getBuildNumber() {
        return 6;
    }

    public String getShortDescription() {
        return "Migrate existing configuration to the IDP_CONFIG table.";
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }

    public Collection<Message> doUpgrade() {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        SsoType ssoType = this.legacySettingsUtil.getLegacySsoType(settings);
        boolean isJira = Objects.equals(this.applicationProperties.getPlatformId(), "jira");
        if (ssoType == SsoType.NONE) {
            log.info("No SSO is enabled, enabling login form");
            PluginSettingsUtil.setBooleanValue(settings, "show-login-form", true);
            if (isJira) {
                PluginSettingsUtil.setBooleanValue(settings, "show-login-form-for-jsm", true);
            }
        } else {
            AbstractIdpConfig.Builder configBuilder;
            log.info("SSO is enabled, creating new IdP");
            boolean redirectOnLogin = PluginSettingsUtil.getBooleanValue(settings, "redirect-on-login", false);
            PluginSettingsUtil.setBooleanValue(settings, "show-login-form", !redirectOnLogin);
            boolean enableAuthenticationFallback = PluginSettingsUtil.getBooleanValue(settings, "allow-redirect-override", false);
            PluginSettingsUtil.setBooleanValue(settings, "enable-authentication-fallback", enableAuthenticationFallback);
            ImmutableJustInTimeConfig.Builder jitConfigBuilder = ImmutableJustInTimeConfig.builder();
            if (ssoType == SsoType.OIDC) {
                log.info("Mapping OIDC config");
                configBuilder = this.legacySettingsUtil.mapLegacyOidcConfig(settings);
                jitConfigBuilder.setAdditionalJitScopes(PluginSettingsUtil.getListValue(settings, "additional-jit-scopes", String::valueOf));
            } else {
                log.info("Mapping SAML config");
                configBuilder = this.legacySettingsUtil.mapLegacySamlConfig(settings);
            }
            configBuilder.setEnabled(true);
            String friendlySsoName = this.resolveFriendlySsoName(ssoType, settings);
            configBuilder.setName(friendlySsoName + " SSO");
            String buttonText = "Log in with " + friendlySsoName;
            configBuilder.setButtonText(buttonText);
            log.info("Setting button text to {}", (Object)buttonText);
            IdpConfig configToMigrate = this.legacySettingsUtil.mapGenericLegacyConfig(settings, configBuilder, jitConfigBuilder);
            this.migrateJsmFields(settings, isJira, configToMigrate);
            this.ssoConfigDao.saveIdpConfig(configToMigrate);
        }
        return ImmutableList.of();
    }

    private void migrateJsmFields(PluginSettings settings, boolean isJira, IdpConfig configToMigrate) {
        if (isJira) {
            boolean showLoginFormForJsm = !configToMigrate.isIncludeCustomerLogins();
            log.info("Setting show login form in JSM to {}", (Object)(showLoginFormForJsm ? "enabled" : "disabled"));
            PluginSettingsUtil.setBooleanValue(settings, "show-login-form-for-jsm", showLoginFormForJsm);
        } else {
            log.info("Skipping migration of show login form in JSM as the current product does not have JSM enabled");
        }
    }

    private String resolveFriendlySsoName(SsoType ssoType, PluginSettings settings) {
        switch (ssoType) {
            case SAML: {
                SamlConfig.IdpType idpType = this.legacySettingsUtil.getIdpType(settings);
                return idpType == SamlConfig.IdpType.CROWD ? "Crowd" : "SAML";
            }
            case OIDC: {
                return "OpenID Connect";
            }
        }
        throw new IllegalArgumentException("Unknown SSO type " + (Object)((Object)ssoType));
    }
}

