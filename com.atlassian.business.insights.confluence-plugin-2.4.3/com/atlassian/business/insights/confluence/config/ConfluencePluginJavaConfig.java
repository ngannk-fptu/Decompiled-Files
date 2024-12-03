/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.cluster.ClusterInfo
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService
 *  com.atlassian.business.insights.api.user.RequestContext
 *  com.atlassian.business.insights.core.config.SystemThenSalPropertiesProvider
 *  com.atlassian.business.insights.core.dataset.DefaultDatasetProvider
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.search.v2.SearchFilter
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.plugins.osgi.javaconfig.annotations.ConditionalOnClass
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.business.insights.confluence.config;

import com.atlassian.business.insights.api.cluster.ClusterInfo;
import com.atlassian.business.insights.api.config.PropertiesProvider;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService;
import com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService;
import com.atlassian.business.insights.api.user.RequestContext;
import com.atlassian.business.insights.confluence.afc.AfcPluginTracker;
import com.atlassian.business.insights.confluence.config.FactoryBeanUtil;
import com.atlassian.business.insights.confluence.config.NewConfluenceSearchQueryFilteringApiAvailableCondition;
import com.atlassian.business.insights.confluence.dataset.AfcAwareDatasetV1;
import com.atlassian.business.insights.confluence.dataset.ConfluenceDatasetV1;
import com.atlassian.business.insights.confluence.extract.CommentLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.CommentToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.IndexValidator;
import com.atlassian.business.insights.confluence.extract.PageLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.PageToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.SpaceLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.SpaceToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.UserLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.UserToLogRecordConverter;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.business.insights.confluence.prefetch.SearchFilterBasedEntityPrefetchProvider;
import com.atlassian.business.insights.confluence.prefetch.SearchQueryBasedEntityPrefetchProvider;
import com.atlassian.business.insights.confluence.spi.ConfluenceClusterInfo;
import com.atlassian.business.insights.confluence.spi.ConfluenceOptOutEntitiesService;
import com.atlassian.business.insights.confluence.spi.ConfluenceRequestContext;
import com.atlassian.business.insights.core.config.SystemThenSalPropertiesProvider;
import com.atlassian.business.insights.core.dataset.DefaultDatasetProvider;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.v2.SearchFilter;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.annotations.ConditionalOnClass;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfluencePluginJavaConfig {
    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public PropertiesProvider propertiesProvider(ApplicationProperties applicationProperties) {
        return new SystemThenSalPropertiesProvider(applicationProperties);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return (TransactionTemplate)OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public ClusterManager clusterManager() {
        return (ClusterManager)OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    public IndexManager indexManager() {
        return (IndexManager)OsgiServices.importOsgiService(IndexManager.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return (I18nResolver)OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public SearchManager searchManager() {
        return (SearchManager)OsgiServices.importOsgiService(SearchManager.class);
    }

    @Bean
    public PageManager pageManager() {
        return (PageManager)OsgiServices.importOsgiService(PageManager.class);
    }

    @Bean
    public ClusterInfo confluenceClusterInfo(ClusterManager clusterManager) {
        return new ConfluenceClusterInfo(clusterManager);
    }

    @Bean
    public IndexValidator indexValidator(IndexManager indexManager, I18nResolver i18nResolver) {
        return new IndexValidator(indexManager, i18nResolver);
    }

    @Bean
    public PageToLogRecordConverter pageToLogRecordConverter(ApplicationProperties applicationProperties) {
        return new PageToLogRecordConverter(applicationProperties);
    }

    @Bean
    public SpaceToLogRecordConverter spaceToLogRecordConverter(ApplicationProperties applicationProperties) {
        return new SpaceToLogRecordConverter(applicationProperties);
    }

    @Bean
    public CommentToLogRecordConverter commentToLogRecordConverter(ApplicationProperties applicationProperties) {
        return new CommentToLogRecordConverter(applicationProperties);
    }

    @Bean
    public UserToLogRecordConverter userToLogRecordConverter(ApplicationProperties applicationProperties) {
        return new UserToLogRecordConverter(applicationProperties);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportClusterInfo(ClusterInfo clusterInfo) {
        return OsgiServices.exportOsgiService((Object)clusterInfo, (ExportOptions)ExportOptions.as(ClusterInfo.class, (Class[])new Class[0]));
    }

    @Bean
    public AfcPluginTracker afcPluginTracker(BundleContext bundleContext) {
        return new AfcPluginTracker(bundleContext);
    }

    @Bean
    @ConditionalOnClass(value={SearchFilter.class})
    public FactoryBean<SearchFilterBasedEntityPrefetchProvider> searchFilterBasedEntityPrefetchProvider(@Nonnull SearchManager searchManager, @Nonnull TransactionTemplate transactionTemplate) {
        return FactoryBeanUtil.buildFactoryBean(new SearchFilterBasedEntityPrefetchProvider(searchManager, transactionTemplate));
    }

    @Bean
    @Conditional(value={NewConfluenceSearchQueryFilteringApiAvailableCondition.class})
    public FactoryBean<SearchQueryBasedEntityPrefetchProvider> searchQueryBasedEntityPrefetchProvider(@Nonnull SearchManager searchManager, @Nonnull TransactionTemplate transactionTemplate) {
        return FactoryBeanUtil.buildFactoryBean(new SearchQueryBasedEntityPrefetchProvider(searchManager, transactionTemplate));
    }

    @Bean
    public EntityPrefetchProvider entityPrefetchProvider(@Nullable SearchFilterBasedEntityPrefetchProvider searchFilterBasedEntityPrefetchProvider, @Nullable SearchQueryBasedEntityPrefetchProvider searchQueryBasedEntityPrefetchProvider) {
        if (Objects.isNull(searchQueryBasedEntityPrefetchProvider)) {
            return searchFilterBasedEntityPrefetchProvider;
        }
        return searchQueryBasedEntityPrefetchProvider;
    }

    @Bean
    public ConfluenceRequestContext confluenceRequestContext() {
        return new ConfluenceRequestContext();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConfluenceRequestContext(ConfluenceRequestContext confluenceRequestContext) {
        return OsgiServices.exportOsgiService((Object)confluenceRequestContext, (ExportOptions)ExportOptions.as(RequestContext.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportPropertiesProvider(PropertiesProvider propertiesProvider) {
        return OsgiServices.exportOsgiService((Object)propertiesProvider, (ExportOptions)ExportOptions.as(PropertiesProvider.class, (Class[])new Class[0]));
    }

    @Bean
    public Dataset datasetV1(@Nonnull IndexValidator indexValidator, @Nonnull TransactionTemplate transactionTemplate, @Nonnull @Qualifier(value="entityPrefetchProvider") EntityPrefetchProvider entityPrefetchProvider, @Nonnull PageToLogRecordConverter pageConverter, @Nonnull SpaceToLogRecordConverter spaceConverter, @Nonnull CommentToLogRecordConverter commentConverter, @Nonnull UserToLogRecordConverter userConverter, @Nonnull PageManager pageManager, @Nonnull AfcPluginTracker afcPluginTracker, @Nonnull ApplicationProperties applicationProperties) {
        return new AfcAwareDatasetV1(new ConfluenceDatasetV1(new SpaceLogRecordStreamer(indexValidator, spaceConverter, entityPrefetchProvider), new PageLogRecordStreamer(indexValidator, pageConverter, transactionTemplate, entityPrefetchProvider, pageManager), new CommentLogRecordStreamer(indexValidator, commentConverter, entityPrefetchProvider), new UserLogRecordStreamer(indexValidator, userConverter, entityPrefetchProvider)), afcPluginTracker, applicationProperties);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportDatasetProvider(List<Dataset> datasets) {
        return OsgiServices.exportOsgiService((Object)new DefaultDatasetProvider(datasets), (ExportOptions)ExportOptions.as(DatasetProvider.class, (Class[])new Class[0]));
    }

    @Bean
    public ConfluenceOptOutEntitiesService confluenceOptOutEntitiesService(@Nonnull ApplicationProperties applicationProperties, @Nonnull SearchManager searchManager, @Nonnull @Qualifier(value="entityPrefetchProvider") EntityPrefetchProvider entityPrefetchProvider) {
        return new ConfluenceOptOutEntitiesService(applicationProperties, searchManager, entityPrefetchProvider);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportOptOutEntitiesLookupService(ConfluenceOptOutEntitiesService confluenceOptOutResourcesService) {
        return OsgiServices.exportOsgiService((Object)confluenceOptOutResourcesService, (ExportOptions)ExportOptions.as(OptOutEntitiesLookupService.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportOptOutEntitiesTransformationService(ConfluenceOptOutEntitiesService confluenceOptOutResourcesService) {
        return OsgiServices.exportOsgiService((Object)confluenceOptOutResourcesService, (ExportOptions)ExportOptions.as(OptOutEntitiesTransformationService.class, (Class[])new Class[0]));
    }
}

