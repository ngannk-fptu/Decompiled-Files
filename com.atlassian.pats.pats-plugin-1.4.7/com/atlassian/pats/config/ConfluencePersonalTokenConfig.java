/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.audit.AuditService
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.AbstractUserProfileAction
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.pats.config;

import com.atlassian.audit.api.AuditService;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.pats.access.services.ConfluenceReadOnlyModeService;
import com.atlassian.pats.access.services.ReadOnlyModeService;
import com.atlassian.pats.checker.ConfluenceProductUserProvider;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.entrypoint.ConfluenceProfilePersonalAccessTokenView;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.events.audit.confluence.ConfluenceAdvancedAuditLogHandler;
import com.atlassian.pats.events.audit.confluence.ConfluenceLegacyAuditLogHandler;
import com.atlassian.pats.notifications.mail.services.ConfluenceMailService;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import com.atlassian.pats.utils.ConfluenceHelper;
import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={ConfluenceOnly.class})
public class ConfluencePersonalTokenConfig {
    private static final Logger log = LoggerFactory.getLogger(ConfluencePersonalTokenConfig.class);

    @Bean
    public AbstractUserProfileAction confluenceProfilePersonalAccessTokenView() {
        return new ConfluenceProfilePersonalAccessTokenView();
    }

    @Bean
    public UserAccessor searchManager() {
        return OsgiServices.importOsgiService(UserAccessor.class);
    }

    @Bean
    public ProductUserProvider userProvider(UserAccessor userAccessor) {
        return new ConfluenceProductUserProvider(userAccessor);
    }

    @Bean
    public MultiQueueTaskManager multiQueueTaskManager() {
        return OsgiServices.importOsgiService(MultiQueueTaskManager.class);
    }

    @Bean
    public ProductMailService productMailService(MultiQueueTaskManager multiQueueTaskManager) {
        return new ConfluenceMailService(multiQueueTaskManager);
    }

    @Bean
    public ProductHelper productHelper(ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        return new ConfluenceHelper(applicationProperties, i18nResolver);
    }

    @Bean
    public AuditLogHandler auditLogHandler(I18nResolver i18nResolver, UserAccessor userAccessor) {
        try {
            return new ConfluenceAdvancedAuditLogHandler(OsgiServices.importOsgiService(AuditService.class), i18nResolver, userAccessor);
        }
        catch (Exception | NoClassDefFoundError e) {
            log.warn("Advanced AuditService not available - will use Legacy AuditService instead");
            return new ConfluenceLegacyAuditLogHandler(OsgiServices.importOsgiService(com.atlassian.confluence.api.service.audit.AuditService.class), i18nResolver, userAccessor, OsgiServices.importOsgiService(I18NBeanFactory.class), OsgiServices.importOsgiService(LocaleManager.class));
        }
    }

    @Bean
    public AccessModeService accessModeService() {
        return OsgiServices.importOsgiService(AccessModeService.class);
    }

    @Bean
    public ReadOnlyModeService readOnlyModeService(AccessModeService accessModeService) {
        return new ConfluenceReadOnlyModeService(accessModeService);
    }
}

