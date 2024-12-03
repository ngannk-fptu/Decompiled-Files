/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.google.inject.AbstractModule
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal.guice;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.google.inject.AbstractModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SalModule
extends AbstractModule {
    private final ApplicationProperties applicationProperties;
    private final TrustedApplicationsManager trustedApplicationsManager;
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public SalModule(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport TrustedApplicationsManager trustedApplicationsManager, @ComponentImport UserManager userManager, @ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.applicationProperties = applicationProperties;
        this.trustedApplicationsManager = trustedApplicationsManager;
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    protected void configure() {
        this.bind(ApplicationProperties.class).toInstance((Object)this.applicationProperties);
        this.bind(TrustedApplicationsManager.class).toInstance((Object)this.trustedApplicationsManager);
        this.bind(UserManager.class).toInstance((Object)this.userManager);
        this.bind(PluginSettingsFactory.class).toInstance((Object)this.pluginSettingsFactory);
    }
}

