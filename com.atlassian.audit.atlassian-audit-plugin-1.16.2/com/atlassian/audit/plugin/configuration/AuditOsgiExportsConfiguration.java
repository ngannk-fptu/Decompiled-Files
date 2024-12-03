/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditCoverageConfigService
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.api.AuditSearchService
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.api.AuditCoverageConfigService;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.plugin.configuration.PermissionsEnforced;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;

public class AuditOsgiExportsConfiguration {
    @Bean
    public FactoryBean<ServiceRegistration> exportAuditCoverageService(@PermissionsEnforced AuditCoverageConfigService configService) {
        return OsgiServices.exportOsgiService(configService, ExportOptions.as(AuditCoverageConfigService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAuditRetentionService(@PermissionsEnforced AuditRetentionConfigService auditRetentionConfigService) {
        return OsgiServices.exportOsgiService(auditRetentionConfigService, ExportOptions.as(AuditRetentionConfigService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAuditSearchService(AuditSearchService auditSearchService) {
        return OsgiServices.exportOsgiService(auditSearchService, ExportOptions.as(AuditSearchService.class, new Class[0]));
    }
}

