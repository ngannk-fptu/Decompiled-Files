/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.confluence.plugins.metadata.jira.osgi;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.net.RequestFactory;

public class ComponentImports {
    private final ConfluenceWebResourceManager webResourceManager;
    private final EventPublisher eventPublisher;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final HostApplication hostApplication;
    private final I18NBeanFactory i18NBeanFactory;
    private final CacheManager cacheManager;
    private final RequestFactory requestFactory;
    private final LinkedApplicationCapabilities linkedApplicationCapabilities;
    private final ApplicationProperties applicationProperties;
    private final OutboundWhitelist outboundWhitelist;

    public ComponentImports(@ComponentImport ConfluenceWebResourceManager webResourceManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport ReadOnlyApplicationLinkService applicationLinkService, @ComponentImport HostApplication hostApplication, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport CacheManager cacheManager, @ComponentImport RequestFactory requestFactory, @ComponentImport LinkedApplicationCapabilities linkedApplicationCapabilities, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport OutboundWhitelist outboundWhitelist) {
        this.webResourceManager = webResourceManager;
        this.eventPublisher = eventPublisher;
        this.applicationLinkService = applicationLinkService;
        this.hostApplication = hostApplication;
        this.i18NBeanFactory = i18NBeanFactory;
        this.cacheManager = cacheManager;
        this.requestFactory = requestFactory;
        this.linkedApplicationCapabilities = linkedApplicationCapabilities;
        this.applicationProperties = applicationProperties;
        this.outboundWhitelist = outboundWhitelist;
    }
}

