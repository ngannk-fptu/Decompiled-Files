/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerTokenStore
 *  com.atlassian.oauth.consumer.core.ConsumerServiceImpl
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore
 *  com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth.consumer.sal.spring;

import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerTokenStore;
import com.atlassian.oauth.consumer.core.ConsumerServiceImpl;
import com.atlassian.oauth.consumer.core.ConsumerServiceStore;
import com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider;
import com.atlassian.oauth.consumer.sal.KeyPairFactory;
import com.atlassian.oauth.consumer.sal.KeyPairFactoryImpl;
import com.atlassian.oauth.consumer.sal.PluginSettingsConsumerServiceStore;
import com.atlassian.oauth.consumer.sal.PluginSettingsConsumerTokenStore;
import com.atlassian.oauth.consumer.sal.PluginSettingsHostConsumerAndSecretProviderImpl;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
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
    public I18nResolver i18nResolver() {
        return (I18nResolver)OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public KeyPairFactory keyPairFactory() {
        return new KeyPairFactoryImpl();
    }

    @Bean
    public ConsumerServiceStore consumerServiceStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsConsumerServiceStore(pluginSettingsFactory);
    }

    @Bean
    public HostConsumerAndSecretProvider hostConsumerAndSecretProvider(ApplicationProperties applicationProperties, PluginSettingsFactory pluginSettingsFactory, KeyPairFactory keyPairFactory, I18nResolver i18nResolver) {
        return new PluginSettingsHostConsumerAndSecretProviderImpl(applicationProperties, pluginSettingsFactory, keyPairFactory, i18nResolver);
    }

    @Bean
    public ConsumerService consumerService(ConsumerServiceStore store, ConsumerTokenStore tokenStore, HostConsumerAndSecretProvider hostCasProvider) {
        return new ConsumerServiceImpl(store, tokenStore, hostCasProvider);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConsumerService(ConsumerService consumerService) {
        return OsgiServices.exportOsgiService((Object)consumerService, (ExportOptions)ExportOptions.as(ConsumerService.class, (Class[])new Class[0]));
    }

    @Bean
    public ConsumerTokenStore consumerTokenStore(PluginSettingsFactory pluginSettingsFactory, ConsumerServiceStore consumerServiceStore, HostConsumerAndSecretProvider hostConsumerAndSecretProvider) {
        return new PluginSettingsConsumerTokenStore(pluginSettingsFactory, consumerServiceStore, hostConsumerAndSecretProvider);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConsumerTokenStore(ConsumerTokenStore consumerTokenStore) {
        return OsgiServices.exportOsgiService((Object)consumerTokenStore, (ExportOptions)ExportOptions.as(ConsumerTokenStore.class, (Class[])new Class[0]));
    }
}

