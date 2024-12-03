/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.datetime.DateFormatService
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.core.BatchOperationManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.search.v2.CustomSearchIndexRegistry
 *  com.atlassian.confluence.search.v2.SearchIndexAccessor
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.confluence.plugins.tasklist;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.cache.CacheManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.datetime.DateFormatService;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.api.user.UserManager;

public class ComponentsImport {
    @ComponentImport
    ContentService contentService;
    @ComponentImport
    SpaceService spaceService;
    @ComponentImport
    CacheManager cacheManager;
    @ComponentImport
    UserAccessor userAccessor;
    @ComponentImport
    LocaleManager localeManager;
    @ComponentImport
    I18nResolver i18nResolver;
    @ComponentImport
    TimeZoneManager timeZoneManager;
    @ComponentImport
    UserManager salUserManager;
    @ComponentImport
    DataSourceFactory dataSourceFactory;
    @ComponentImport
    TemplateRenderer templateRenderer;
    @ComponentImport
    FeatureDiscoveryService featureDiscoveryService;
    @ComponentImport
    ActiveObjects ao;
    @ComponentImport
    TransactionTemplate transactionTemplate;
    @ComponentImport
    PageManager pageManager;
    @ComponentImport
    XmlEventReaderFactory xmlEventReaderFactory;
    @ComponentImport
    XhtmlContent xhtmlContent;
    @ComponentImport
    I18NBeanFactory i18NBeanFactory;
    @ComponentImport
    MarshallingRegistry marshallingRegistry;
    @ComponentImport
    DraftManager draftManagerTarget;
    @ComponentImport
    XMLEventFactoryProvider xmlEventFactoryProvider;
    @ComponentImport
    PermissionManager permissionManagerTarget;
    @ComponentImport
    PaginationService apiPaginationService;
    @ComponentImport
    EventPublisher eventPublisher;
    @ComponentImport
    ApplicationConfiguration applicationConfiguration;
    @ComponentImport
    DateFormatService apiDateFormatServiceTarget;
    @ComponentImport
    ContentEntityManager contentEntityManager;
    @ComponentImport
    CustomContentManager customContentManager;
    @ComponentImport
    SpaceManager spaceManager;
    @ComponentImport
    LabelManager labelManager;
    @ComponentImport
    FormatSettingsManager formatSettingsManager;
    @ComponentImport
    SpacePermissionManager spacePermissionManager;
    @ComponentImport
    ContentService apiContentService;
    @ComponentImport(value="xmlOutputFactory")
    XmlOutputFactory xmlOutputFactory;
    @ComponentImport(value="xmlFragmentOutputFactory")
    XmlOutputFactory xmlFragmentOutputFactory;
    @ComponentImport
    WebResourceUrlProvider webResourceUrlProvider;
    @ComponentImport
    ConfluenceAccessManager confluenceAccessManager;
    @ComponentImport
    DarkFeatureManager darkFeatureManager;
    @ComponentImport
    NotificationUserService notificationUserService;
    @ComponentImport
    ConfluenceIndexer confluenceIndexer;
    @ComponentImport
    BatchOperationManager batchOperationManager;
    @ComponentImport
    SearchIndexAccessor searchIndexAccessor;
    @ComponentImport
    CustomSearchIndexRegistry customSearchIndexRegistry;
    @ComponentImport
    ContentPermissionCalculator contentPermissionCalculator;
    @ComponentImport
    PredefinedSearchBuilder predefinedSearchBuilder;
    @ComponentImport
    PluginUpgradeTask pluginUpgradeTask;
}

