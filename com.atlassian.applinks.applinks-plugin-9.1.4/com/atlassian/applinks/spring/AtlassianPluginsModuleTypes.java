/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.SupportedInboundAuthenticationModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.spring;

import com.atlassian.applinks.core.plugin.ApplicationTypeModuleDescriptor;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.core.plugin.EntityTypeModuleDescriptor;
import com.atlassian.applinks.host.spi.SupportedInboundAuthenticationModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginsModuleTypes {
    @Bean
    public FactoryBean<ServiceRegistration> exportApplinkApplicationTypeModuleType(HostContainer hostContainer) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)new SingleModuleDescriptorFactory(hostContainer, "applinks-application-type", ApplicationTypeModuleDescriptor.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplinkAuthenticationProviderModuleType(HostContainer hostContainer) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)new SingleModuleDescriptorFactory(hostContainer, "applinks-authentication-provider", AuthenticationProviderModuleDescriptor.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplinkEntityTypeModuleType(HostContainer hostContainer) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)new SingleModuleDescriptorFactory(hostContainer, "applinks-entity-type", EntityTypeModuleDescriptor.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSupportedInboundAuthenticationModuleType(HostContainer hostContainer) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)new SingleModuleDescriptorFactory(hostContainer, "supported-inbound-authentication", SupportedInboundAuthenticationModuleDescriptor.class));
    }
}

