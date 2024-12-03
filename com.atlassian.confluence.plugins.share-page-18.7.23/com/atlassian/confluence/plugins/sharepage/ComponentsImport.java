/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.MailContentProcessor
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.notifications.api.medium.ServerManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.sharepage;

import com.atlassian.audit.api.AuditService;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.MailContentProcessor;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ComponentsImport {
    @ComponentImport
    MailContentProcessor mailContentProcessor;
    @ComponentImport
    I18nResolver i18nResolver;
    @ComponentImport
    TransactionTemplate transactionTemplate;
    @ComponentImport
    ServerManager serverManager;
    @ComponentImport
    ContentEntityManager contentEntityManager;
    @ComponentImport
    AttachmentManager attachmentManager;
    @ComponentImport
    UserAccessor userAccessor;
    @ComponentImport
    LocaleManager localeManager;
    @ComponentImport
    I18NBeanFactory i18NBeanFactory;
    @ComponentImport
    EventPublisher eventPublisher;
    @ComponentImport
    DataSourceFactory dataSourceFactory;
    @ComponentImport
    LongRunningTaskManager longRunningTaskManager;
    @ComponentImport
    FormatSettingsManager formatSettingsManager;
    @ComponentImport
    SignupManager signupManager;
    @ComponentImport
    PermissionManager permissionManager;
    @ComponentImport
    ContentUiSupport contentUiSupport;
    @ComponentImport
    DarkFeaturesManager darkFeaturesManager;
    @ComponentImport
    StandardAuditResourceTypes standardAuditResourceTypes;
    @ComponentImport
    AuditService auditService;
}

