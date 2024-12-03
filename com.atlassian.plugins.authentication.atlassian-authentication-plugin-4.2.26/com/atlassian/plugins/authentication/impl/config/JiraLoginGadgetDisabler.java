/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.event.LoginFormToggledEvent;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JiraComponent
public class JiraLoginGadgetDisabler
implements LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(JiraLoginGadgetDisabler.class);
    public static final String PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    public static final String LOGIN_GADGET_INITIALLY_DISABLED = "jira.login.gadget.initially.disabled";
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;
    private final PluginSettingsFactory pluginSettings;
    private final SsoConfigService ssoConfigService;

    @Inject
    public JiraLoginGadgetDisabler(@JiraImport(value="jiraApplicationProperties") ApplicationProperties applicationProperties, @ComponentImport EventPublisher eventPublisher, @ComponentImport PluginSettingsFactory pluginSettings, SsoConfigService ssoConfigService) {
        this.applicationProperties = applicationProperties;
        this.eventPublisher = eventPublisher;
        this.pluginSettings = pluginSettings;
        this.ssoConfigService = ssoConfigService;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void onStart() {
        if (!this.ssoConfigService.getSsoConfig().getShowLoginForm()) {
            this.disableLoginGadget();
        }
    }

    public void onStop() {
    }

    @EventListener
    public void onLoginFormToggled(LoginFormToggledEvent event) {
        if (event.isEnabled()) {
            logger.info("Enabling Jira login gadget as the login form is available.");
            this.restoreLoginGadgetInitialState();
        } else {
            logger.info("Disabling Jira login gadget as the login form is disabled.");
            this.disableLoginGadget();
        }
    }

    private PluginSettings settings() {
        return this.pluginSettings.createGlobalSettings();
    }

    private void restoreLoginGadgetInitialState() {
        PluginSettingsUtil.getBooleanValue(this.settings(), LOGIN_GADGET_INITIALLY_DISABLED).ifPresent(loginGadgetInitiallyDisabled -> {
            this.applicationProperties.setOption("jira.disable.login.gadget", loginGadgetInitiallyDisabled.booleanValue());
            logger.info("Restored login gadget state to: {}", (Object)(loginGadgetInitiallyDisabled != false ? "disabled" : "enabled"));
        });
        this.settings().remove("com.atlassian.plugins.authentication.sso.config.jira.login.gadget.initially.disabled");
    }

    private void disableLoginGadget() {
        boolean loginGadgetInitiallyDisabled = PluginSettingsUtil.getBooleanValue(this.settings(), LOGIN_GADGET_INITIALLY_DISABLED).orElse(this.applicationProperties.getOption("jira.disable.login.gadget"));
        PluginSettingsUtil.setBooleanValue(this.settings(), LOGIN_GADGET_INITIALLY_DISABLED, loginGadgetInitiallyDisabled);
        this.applicationProperties.setOption("jira.disable.login.gadget", true);
        logger.info("Disabled login gadget. Initial state was: {}", (Object)(loginGadgetInitiallyDisabled ? "disabled" : "enabled"));
    }
}

