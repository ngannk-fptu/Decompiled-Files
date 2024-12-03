/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model.Settings;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditingService {
    private static final Logger log = LoggerFactory.getLogger(AuditingService.class);
    private static final String PAGES_AUDITING_CATEGORY_KEY = "audit.logging.category.pages";
    private static final String ADMIN_AUDITING_CATEGORY_KEY = "audit.logging.category.admin";
    private static final String EMPTY_TITLE_KEY = "untitled.content.render.title";
    private static final String REPORTS_SETTINGS_KEY = "collaborative.editing.feedback.admin.settings.editor.reports.enabled";
    private final AuditService auditService;
    private final StandardAuditResourceTypes resourceTypes;
    private final PageManager pageManager;
    private final I18nResolver i18nResolver;

    @Autowired
    public AuditingService(@ComponentImport AuditService auditService, @ComponentImport StandardAuditResourceTypes resourceTypes, @ComponentImport PageManager pageManager, @ComponentImport I18nResolver i18nResolver) {
        this.auditService = auditService;
        this.resourceTypes = resourceTypes;
        this.pageManager = pageManager;
        this.i18nResolver = i18nResolver;
    }

    public void audit(long contentId, String actionI18NKey) {
        try {
            AuditType auditType = AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)CoverageLevel.BASE, (String)PAGES_AUDITING_CATEGORY_KEY, (String)actionI18NKey).build();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)auditType);
            AbstractPage affectedPage = this.pageManager.getAbstractPage(contentId);
            if (affectedPage != null) {
                String name = (String)StringUtils.defaultIfBlank((CharSequence)affectedPage.getTitle(), (CharSequence)this.i18nResolver.getText(EMPTY_TITLE_KEY));
                String type = affectedPage.getTypeEnum() == ContentTypeEnum.PAGE ? this.resourceTypes.page() : this.resourceTypes.blog();
                auditEventBuilder.affectedObject(AuditResource.builder((String)name, (String)type).id(String.valueOf(contentId)).build());
            }
            this.auditService.audit(auditEventBuilder.build());
        }
        catch (Exception e) {
            log.error("Error adding audit event for action {} on the page {}: {}", new Object[]{actionI18NKey, contentId, e});
            log.debug("Error adding audit event for action {} on the page {}", new Object[]{actionI18NKey, contentId, e});
        }
    }

    public void audit(Settings oldSettings, Settings newSettings, String actionI18NKey) {
        try {
            AuditType auditType = AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.BASE, (String)ADMIN_AUDITING_CATEGORY_KEY, (String)actionI18NKey).build();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)auditType);
            auditEventBuilder.changedValue(ChangedValue.fromI18nKeys((String)REPORTS_SETTINGS_KEY).from(String.valueOf(oldSettings.isEditorReportsEnabled())).to(String.valueOf(newSettings.isEditorReportsEnabled())).build());
            this.auditService.audit(auditEventBuilder.build());
        }
        catch (Exception e) {
            log.error("Error adding audit event for settings update: {}", (Object)e.toString());
            log.debug("Error adding audit event for settings update", (Throwable)e);
        }
    }
}

