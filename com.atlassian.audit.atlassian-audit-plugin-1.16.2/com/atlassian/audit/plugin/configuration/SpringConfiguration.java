/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  org.codehaus.jackson.JsonGenerator$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.LicenseDowngradeListener;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.permission.SysPropBasedPermissionChecker;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.configuration.AuditBrokerConfiguration;
import com.atlassian.audit.plugin.configuration.AuditCacheConfiguration;
import com.atlassian.audit.plugin.configuration.AuditConfigConfiguration;
import com.atlassian.audit.plugin.configuration.AuditConsumersConfiguration;
import com.atlassian.audit.plugin.configuration.AuditExportConfiguration;
import com.atlassian.audit.plugin.configuration.AuditOnboardingConfiguration;
import com.atlassian.audit.plugin.configuration.AuditOsgiExportsConfiguration;
import com.atlassian.audit.plugin.configuration.AuditOsgiImportsConfiguration;
import com.atlassian.audit.plugin.configuration.AuditSearchConfiguration;
import com.atlassian.audit.plugin.configuration.AuditUpgradeConfiguration;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.service.DefaultTranslationService;
import com.atlassian.audit.service.TranslationService;
import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={AuditBrokerConfiguration.class, AuditCacheConfiguration.class, AuditConfigConfiguration.class, AuditConsumersConfiguration.class, AuditExportConfiguration.class, AuditOnboardingConfiguration.class, AuditOsgiExportsConfiguration.class, AuditOsgiImportsConfiguration.class, AuditSearchConfiguration.class, AuditUpgradeConfiguration.class})
public class SpringConfiguration {
    @Bean
    public ProductLicenseChecker licenseChecker(LicenseHandler licenseHandler) {
        return new ProductLicenseChecker(licenseHandler);
    }

    @Bean
    public PermissionChecker permissionChecker(UserManager userManager, ResourceContextPermissionChecker resourceContextPermissionChecker, PropertiesProvider propertiesProvider) {
        return new SysPropBasedPermissionChecker(userManager, resourceContextPermissionChecker, propertiesProvider);
    }

    @Bean
    public LicenseDowngradeListener licenseDowngradeListener(ProductLicenseChecker licenseChecker, @PermissionsNotEnforced InternalAuditCoverageConfigService configServiceSupplier, ExcludedActionsService excludedActionsService, EventListenerRegistrar eventListenerRegistrar) {
        return new LicenseDowngradeListener(licenseChecker, configServiceSupplier, excludedActionsService, eventListenerRegistrar);
    }

    @Bean
    public TranslationService translationService(I18nResolver i18nResolver, LocaleResolver localeResolver) {
        return new DefaultTranslationService(i18nResolver, localeResolver);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        return mapper;
    }

    @Bean
    public AuditPluginInfo auditPluginInfo(PluginAccessor pluginAccessor) {
        return new AuditPluginInfo(pluginAccessor);
    }
}

