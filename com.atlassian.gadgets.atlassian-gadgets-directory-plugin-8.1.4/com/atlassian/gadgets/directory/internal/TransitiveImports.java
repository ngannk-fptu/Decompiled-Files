/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransitiveImports {
    private final ApplicationProperties applicationProperties;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final LoginUriProvider loginUriProvider;
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;
    private final SubscribedGadgetFeedStore subscribedGadgetFeedStore;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public TransitiveImports(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport UserManager userManager, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport EventPublisher eventPublisher, @ComponentImport SubscribedGadgetFeedStore SubscribedGadgetFeedStore2, @ComponentImport PluginAccessor pluginAccessor) {
        this.applicationProperties = applicationProperties;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.loginUriProvider = loginUriProvider;
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.eventPublisher = eventPublisher;
        this.subscribedGadgetFeedStore = SubscribedGadgetFeedStore2;
        this.pluginAccessor = pluginAccessor;
    }
}

