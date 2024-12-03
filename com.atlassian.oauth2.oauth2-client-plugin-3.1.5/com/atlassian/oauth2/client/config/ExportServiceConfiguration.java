/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.lib.token.TokenService
 *  com.atlassian.oauth2.client.api.storage.TokenHandler
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.client.config;

import com.atlassian.oauth2.client.analytics.StatisticsCollectionService;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.token.TokenService;
import com.atlassian.oauth2.client.api.storage.TokenHandler;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.jobs.TokenPruningJob;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportServiceConfiguration {
    @Bean
    public FactoryBean<ServiceRegistration> exportClientTokenStorageService(ClientTokenStorageService clientTokenStorageService) {
        return OsgiServices.exportOsgiService(clientTokenStorageService, ExportOptions.as(ClientTokenStorageService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportClientConfigStorageService(ClientConfigStorageService clientConfigStorageService) {
        return OsgiServices.exportOsgiService(clientConfigStorageService, ExportOptions.as(ClientConfigStorageService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTokenService(TokenService tokenService) {
        return OsgiServices.exportOsgiService(tokenService, ExportOptions.as(TokenService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportFlowRequestService(FlowRequestService flowRequestService) {
        return OsgiServices.exportOsgiService(flowRequestService, ExportOptions.as(FlowRequestService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTokenHandler(TokenHandler tokenHandler) {
        return OsgiServices.exportOsgiService(tokenHandler, ExportOptions.as(TokenHandler.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTokenPruningJobAsLifecycleAware(TokenPruningJob tokenPruningJob) {
        return OsgiServices.exportOsgiService(tokenPruningJob, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportStatisticsCollectionServiceAsLifecycleAware(StatisticsCollectionService statisticsCollectionService) {
        return OsgiServices.exportOsgiService(statisticsCollectionService, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }
}

