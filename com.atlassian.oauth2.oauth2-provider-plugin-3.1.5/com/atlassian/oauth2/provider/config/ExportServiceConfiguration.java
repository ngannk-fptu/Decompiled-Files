/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.oauth2.provider.api.settings.JwtSecretInitService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.provider.config;

import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.provider.api.settings.JwtSecretInitService;
import com.atlassian.oauth2.provider.core.client.SystemAdminProtectedClientService;
import com.atlassian.oauth2.provider.core.jobs.RemoveExpiredAuthorizationsJob;
import com.atlassian.oauth2.provider.core.jobs.RemoveExpiredTokensJob;
import com.atlassian.oauth2.provider.core.jobs.StatisticsJob;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportServiceConfiguration {
    @Bean
    public FactoryBean<ServiceRegistration> exportJwtSecretInitServiceAsLifecycleAware(JwtSecretInitService jwtSecretInitService) {
        return OsgiServices.exportOsgiService(jwtSecretInitService, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportRemoveExpiredAuthorizationsJobAsLifecycleAware(RemoveExpiredAuthorizationsJob removeExpiredAuthorizationsJob) {
        return OsgiServices.exportOsgiService(removeExpiredAuthorizationsJob, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportRemoveExpiredTokensJobAsLifecycleAware(RemoveExpiredTokensJob removeExpiredTokensJob) {
        return OsgiServices.exportOsgiService(removeExpiredTokensJob, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportStatisticsJobAsLifecycleAware(StatisticsJob statisticsJob) {
        return OsgiServices.exportOsgiService(statisticsJob, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportOAuth2ProviderStorageService(OAuth2ProviderService oAuth2ProviderService) {
        return OsgiServices.exportOsgiService(oAuth2ProviderService, ExportOptions.as(OAuth2ProviderService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportClientService(ClientService clientService, PermissionEnforcer permissionEnforcer) {
        return OsgiServices.exportOsgiService(new SystemAdminProtectedClientService(permissionEnforcer, clientService), ExportOptions.as(ClientService.class, new Class[0]));
    }
}

