/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.analytics.client.spring.shared;

import com.atlassian.analytics.client.eventfilter.whitelist.AnalyticsWhitelistModuleDescriptorFactory;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ModuleFactoryBean.class})
public class WhitelistModuleTypeBeans {
    @Bean
    public ListableModuleDescriptorFactory myModuleDescriptorFactory(ModuleFactory moduleFactory) {
        return new AnalyticsWhitelistModuleDescriptorFactory(moduleFactory);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportMyModuleType(ListableModuleDescriptorFactory moduleDescriptorFactory) {
        return OsgiServices.exportAsModuleType(moduleDescriptorFactory);
    }
}

