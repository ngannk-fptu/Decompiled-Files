/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.jira.auditing.AuditingManager
 *  com.atlassian.jira.plugin.profile.ViewProfilePanel
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.mail.queue.MailQueue
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.pats.config;

import com.atlassian.audit.api.AuditService;
import com.atlassian.jira.auditing.AuditingManager;
import com.atlassian.jira.plugin.profile.ViewProfilePanel;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.pats.access.services.JiraReadOnlyModeService;
import com.atlassian.pats.access.services.ReadOnlyModeService;
import com.atlassian.pats.checker.JiraProductUserProvider;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.entrypoint.JiraProfilePersonalAccessTokenView;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.events.audit.jira.JiraAdvancedAuditLogHandler;
import com.atlassian.pats.events.audit.jira.JiraLegacyAuditLogHandler;
import com.atlassian.pats.notifications.mail.services.JiraMailService;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import com.atlassian.pats.utils.JiraHelper;
import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={JiraOnly.class})
public class JiraPersonalTokenConfig {
    private static final Logger log = LoggerFactory.getLogger(JiraPersonalTokenConfig.class);

    @Bean
    public ViewProfilePanel jiraProfilePersonalAccessTokenView() {
        return new JiraProfilePersonalAccessTokenView();
    }

    @Bean(name={"jiraUserManager"})
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public UserKeyService userKeyService() {
        return OsgiServices.importOsgiService(UserKeyService.class);
    }

    @Bean
    public MailQueue mailQueue() {
        return OsgiServices.importOsgiService(MailQueue.class);
    }

    @Bean
    public ProductMailService productMailService(MailQueue mailQueue) {
        return new JiraMailService(mailQueue);
    }

    @Bean
    public ProductUserProvider userProvider(@Qualifier(value="jiraUserManager") UserManager userManager, UserKeyService userKeyService) {
        return new JiraProductUserProvider(userKeyService, userManager);
    }

    @Bean
    public ProductHelper productHelper(ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        return new JiraHelper(applicationProperties, i18nResolver);
    }

    @Bean
    public AuditLogHandler jiraAuditLogHandler(I18nResolver i18nResolver, UserManager userManager) {
        try {
            return new JiraAdvancedAuditLogHandler(OsgiServices.importOsgiService(AuditService.class), i18nResolver, userManager);
        }
        catch (Exception | NoClassDefFoundError e) {
            log.warn("AuditService is not available - will use Legacy AuditingManger instead");
            return new JiraLegacyAuditLogHandler(OsgiServices.importOsgiService(AuditingManager.class), i18nResolver, userManager);
        }
    }

    @Bean
    public ReadOnlyModeService readOnlyModeService() {
        return new JiraReadOnlyModeService();
    }
}

