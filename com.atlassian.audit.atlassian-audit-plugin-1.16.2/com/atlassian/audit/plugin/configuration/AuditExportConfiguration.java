/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.csv.AuditCsvExportService;
import com.atlassian.audit.csv.SelectiveExportLicenseChecker;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.web.context.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditExportConfiguration {
    @Bean
    public AuditCsvExportService auditWriteService(AuditPluginInfo auditPluginInfo, AuditSearchService searchService, AuditService auditService, EventPublisher eventPublisher, HttpContext httpContext, I18nResolver resolver, SelectiveExportLicenseChecker licenseChecker) {
        return new AuditCsvExportService(licenseChecker, searchService, resolver, httpContext, eventPublisher, auditPluginInfo, auditService);
    }

    @Bean
    public SelectiveExportLicenseChecker selectiveExportLicenseChecker(ProductLicenseChecker productLicenseChecker, ApplicationProperties applicationProperties) {
        return new SelectiveExportLicenseChecker(productLicenseChecker, applicationProperties);
    }
}

