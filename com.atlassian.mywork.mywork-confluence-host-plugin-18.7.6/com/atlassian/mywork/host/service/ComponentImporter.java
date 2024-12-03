/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.service.ActionServiceSelector
 *  com.atlassian.mywork.service.ServiceSelector
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 */
package com.atlassian.mywork.host.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.service.ActionServiceSelector;
import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

public class ComponentImporter {
    @ComponentImport
    ContextPathHolder contextPathHolder;
    @ComponentImport
    ActiveObjects ao;
    @ComponentImport
    TemplateRenderer templateRenderer;
    @ComponentImport
    UserManager userManager;
    @ComponentImport
    AuthenticationListener authenticationListener;
    @ComponentImport
    AuthenticationController authenticationController;
    @ComponentImport
    PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    PluginScheduler pluginScheduler;
    @ComponentImport
    ServiceSelector serviceSelector;
    @ComponentImport
    ActionServiceSelector actionServiceSelector;
    @ComponentImport
    LocaleResolver localeResolver;
    @ComponentImport
    LoginUriProvider loginUriProvider;
    @ComponentImport
    EventPublisher eventPublisher;
    @ComponentImport
    PluginAccessor pluginAccessor;
    @ComponentImport
    TransactionTemplate transactionTemplate;
    @ComponentImport
    ApplicationLinkService applicationLinkService;
}

