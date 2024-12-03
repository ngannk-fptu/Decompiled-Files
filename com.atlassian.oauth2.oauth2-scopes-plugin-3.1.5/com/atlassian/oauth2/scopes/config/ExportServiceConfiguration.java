/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.scopes.config;

import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportServiceConfiguration {
    @Bean
    public FactoryBean<ServiceRegistration> exportScopeResolver(ScopeResolver scopeResolver) {
        return OsgiServices.exportOsgiService(scopeResolver, ExportOptions.as(ScopeResolver.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportScopesRequestCache(ScopesRequestCache scopesRequestCache) {
        return OsgiServices.exportOsgiService(scopesRequestCache, ExportOptions.as(ScopesRequestCache.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportScopeDescriptionService(ScopeDescriptionService scopeDescriptionService) {
        return OsgiServices.exportOsgiService(scopeDescriptionService, ExportOptions.as(ScopeDescriptionService.class, new Class[0]));
    }
}

