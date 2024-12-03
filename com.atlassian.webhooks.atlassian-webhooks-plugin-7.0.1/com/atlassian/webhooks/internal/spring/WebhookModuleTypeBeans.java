/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.webhooks.WebhookService
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.webhooks.internal.spring;

import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.internal.Validator;
import com.atlassian.webhooks.internal.module.WebhookModuleDescriptorFactory;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ModuleFactoryBean.class})
public class WebhookModuleTypeBeans {
    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public Validator validator() {
        return (Validator)OsgiServices.importOsgiService(Validator.class);
    }

    @Bean
    public WebhookService webhookService() {
        return (WebhookService)OsgiServices.importOsgiService(WebhookService.class);
    }

    @Bean
    public WebhookModuleDescriptorFactory webhookModuleDescriptorFactory() {
        return new WebhookModuleDescriptorFactory();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebhookModuleDescriptorFactory(WebhookModuleDescriptorFactory moduleDescriptorFactory) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)moduleDescriptorFactory);
    }
}

