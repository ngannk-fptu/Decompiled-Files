/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth.serviceprovider.SystemClock
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth.serviceprovider.sal.spring;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.SystemClock;
import com.atlassian.oauth.serviceprovider.sal.PluginSettingsServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.sal.PluginSettingsServiceProviderTokenStore;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeans {
    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return (PluginSettingsFactory)OsgiServices.importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public UserManager userManager() {
        return (UserManager)OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return (EventPublisher)OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public ServiceProviderTokenStore serviceProviderTokenStore(PluginSettingsFactory pluginSettingsFactory, ServiceProviderConsumerStore consumerStore, UserManager userManager, Clock clock, EventPublisher eventPublisher) {
        return new PluginSettingsServiceProviderTokenStore(pluginSettingsFactory, consumerStore, userManager, clock, eventPublisher);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportServiceProviderTokenStore(ServiceProviderTokenStore serviceProvidertokenStore) {
        return OsgiServices.exportOsgiService((Object)serviceProvidertokenStore, (ExportOptions)ExportOptions.as(ServiceProviderTokenStore.class, (Class[])new Class[0]));
    }

    @Bean
    public ServiceProviderConsumerStore serviceProviderConsumerStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsServiceProviderConsumerStore(pluginSettingsFactory);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportServiceProviderConsumerStore(ServiceProviderConsumerStore serviceProviderConsumerStore) {
        return OsgiServices.exportOsgiService((Object)serviceProviderConsumerStore, (ExportOptions)ExportOptions.as(ServiceProviderConsumerStore.class, (Class[])new Class[0]));
    }

    @Bean
    public Clock clock() {
        return new SystemClock();
    }
}

