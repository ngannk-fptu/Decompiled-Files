/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.httpclient.api.factory.HttpClientFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.webhooks.WebhookPayloadProvider
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.WebhookService
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.webhooks.internal.spring;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.api.factory.HttpClientFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.webhooks.WebhookPayloadProvider;
import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.internal.BuiltInWebhookEnricher;
import com.atlassian.webhooks.internal.DefaultPayloadManager;
import com.atlassian.webhooks.internal.DefaultValidator;
import com.atlassian.webhooks.internal.DefaultWebhookService;
import com.atlassian.webhooks.internal.DiagnosticsPayloadProvider;
import com.atlassian.webhooks.internal.OsgiWebhookHostAccessor;
import com.atlassian.webhooks.internal.PlatformConfigurer;
import com.atlassian.webhooks.internal.Validator;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.WebhookPayloadManager;
import com.atlassian.webhooks.internal.WebhooksLifecycle;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.client.RequestExecutor;
import com.atlassian.webhooks.internal.client.request.DefaultRequestExecutor;
import com.atlassian.webhooks.internal.dao.WebhookDao;
import com.atlassian.webhooks.internal.history.WebhookInvocationHistorian;
import com.atlassian.webhooks.internal.jmx.JmxBootstrap;
import com.atlassian.webhooks.internal.publish.DefaultWebhookDispatcher;
import com.atlassian.webhooks.internal.publish.WebhookDispatcher;
import com.atlassian.webhooks.internal.spring.DaoSpringBeans;
import com.atlassian.webhooks.internal.spring.HistorySpringBeans;
import com.atlassian.webhooks.internal.spring.WebhookModuleTypeBeans;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={DaoSpringBeans.class, HistorySpringBeans.class, PluginAccessorBean.class, WebhookModuleTypeBeans.class})
public class SpringBeans {
    @Bean
    public JmxBootstrap jmxBootstrap(WebhookDispatcher webhookDispatcher, WebhookService webhookService) {
        return new JmxBootstrap(webhookDispatcher, webhookService);
    }

    @Bean
    public PlatformConfigurer platformConfigurer(WebhookInvocationHistorian historian, BundleContext bundleContext) {
        return new PlatformConfigurer(historian, bundleContext);
    }

    @Bean
    public RequestExecutor requestExecutor(HttpClientFactory httpClientFactory) {
        return new DefaultRequestExecutor(httpClientFactory);
    }

    @Bean
    public Validator validator(BundleContext bundleContext) {
        return new DefaultValidator(bundleContext);
    }

    @Bean
    public WebhookDispatcher webhookDispatcher(RequestExecutor requestExecutor) {
        return new DefaultWebhookDispatcher(requestExecutor);
    }

    @Bean
    public WebhookHostAccessor webhookHostAccessor(BundleContext bundleContext) {
        return new OsgiWebhookHostAccessor(bundleContext);
    }

    @Bean
    public WebhookPayloadManager webhookPayloadManager(WebhookHostAccessor hostAccessor) {
        return new DefaultPayloadManager(hostAccessor);
    }

    @Bean
    public WebhookPayloadProvider webhookPayloadProvider() {
        return new DiagnosticsPayloadProvider();
    }

    @Bean
    public BuiltInWebhookEnricher webhookRequestEnricher() {
        return new BuiltInWebhookEnricher();
    }

    @Bean
    public WebhookService webhookService(WebhookDao webhookDao, WebhookDispatcher webhookDispatcher, EventPublisher eventPublisher, WebhookHostAccessor hostAccessor, PluginAccessor pluginAccessor, TransactionTemplate txTemplate, Validator validator, WebhookPayloadManager webhookPayloadManager) {
        return new DefaultWebhookService(webhookDao, webhookDispatcher, eventPublisher, hostAccessor, pluginAccessor, txTemplate, validator, webhookPayloadManager);
    }

    @Bean
    public WebhooksLifecycle webhooksLifecycle(WebhookHostAccessor hostAccessor, List<WebhooksLifecycleAware> services, WebhookService webhookService) {
        return new WebhooksLifecycle(hostAccessor, services, webhookService);
    }

    @Bean
    public ActiveObjects activeObjects() {
        return (ActiveObjects)OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return (EventPublisher)OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public HttpClientFactory httpClientFactory() {
        return (HttpClientFactory)OsgiServices.importOsgiService(HttpClientFactory.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return (SchedulerService)OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return (TransactionTemplate)OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhookHostAccessor(WebhookHostAccessor webhookHostAccessor) {
        return OsgiServices.exportOsgiService((Object)webhookHostAccessor, (ExportOptions)ExportOptions.as(LifecycleAware.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhookPayloadProvider(WebhookPayloadProvider webhookPayloadProvider) {
        return OsgiServices.exportOsgiService((Object)webhookPayloadProvider, (ExportOptions)ExportOptions.as(WebhookPayloadProvider.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhookRequestEnricher(BuiltInWebhookEnricher builtInWebhookEnricher) {
        return OsgiServices.exportOsgiService((Object)builtInWebhookEnricher, (ExportOptions)ExportOptions.as(WebhookRequestEnricher.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhookService(WebhookService webhookService) {
        return OsgiServices.exportOsgiService((Object)webhookService, (ExportOptions)ExportOptions.as(WebhookService.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhooksLifecycle(WebhooksLifecycle webhooksLifecycle) {
        return OsgiServices.exportOsgiService((Object)webhooksLifecycle, (ExportOptions)ExportOptions.as(LifecycleAware.class, (Class[])new Class[0]));
    }
}

