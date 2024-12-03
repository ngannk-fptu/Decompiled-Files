/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.event.LoginFormToggledEvent;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BambooComponent
public class BambooSignupDisabler {
    private static final Logger logger = LoggerFactory.getLogger(BambooSignupDisabler.class);
    private static final String PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    private static final String BAMBOO_SIGNUP_INITIALLY_DISABLED = "bamboo.signup.initially.disabled";
    private final AdministrationConfigurationAccessor administrationConfigurationAccessor;
    private final EventPublisher eventPublisher;
    private final PluginSettingsFactory pluginSettings;

    @Inject
    public BambooSignupDisabler(@BambooImport AdministrationConfigurationAccessor administrationConfigurationAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport PluginSettingsFactory pluginSettings) {
        this.administrationConfigurationAccessor = administrationConfigurationAccessor;
        this.eventPublisher = eventPublisher;
        this.pluginSettings = pluginSettings;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onLoginFormToggled(LoginFormToggledEvent event) {
        if (event.isEnabled()) {
            logger.info("Enabling Bamboo signup as the login form is available.");
            this.restoreBambooSignupInitialState();
        } else {
            logger.info("Disabling Bamboo signup as the login form is disabled.");
            this.disableBambooSignup();
        }
    }

    private PluginSettings settings() {
        return this.pluginSettings.createGlobalSettings();
    }

    private void restoreBambooSignupInitialState() {
        PluginSettingsUtil.getBooleanValue(this.settings(), BAMBOO_SIGNUP_INITIALLY_DISABLED).ifPresent(bambooSignupInitiallyDisabled -> {
            this.administrationConfigurationAccessor.getAdministrationConfiguration().setEnableSignup(bambooSignupInitiallyDisabled.booleanValue());
            logger.info("Restored bamboo signup state to: {}", (Object)(bambooSignupInitiallyDisabled != false ? "disabled" : "enabled"));
        });
        this.settings().remove("com.atlassian.plugins.authentication.sso.config.bamboo.signup.initially.disabled");
    }

    private void disableBambooSignup() {
        boolean bambooSignupInitiallyDisabled = PluginSettingsUtil.getBooleanValue(this.settings(), BAMBOO_SIGNUP_INITIALLY_DISABLED).orElse(this.administrationConfigurationAccessor.getAdministrationConfiguration().isEnableSignup());
        PluginSettingsUtil.setBooleanValue(this.settings(), BAMBOO_SIGNUP_INITIALLY_DISABLED, bambooSignupInitiallyDisabled);
        this.administrationConfigurationAccessor.getAdministrationConfiguration().setEnableSignup(false);
        logger.info("Disabled bamboo signup. Initial state was: {}", (Object)(bambooSignupInitiallyDisabled ? "disabled" : "enabled"));
    }
}

