/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.event.events.search.SiteSearchAuditEvent
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.event.events.search.SiteSearchAuditEvent;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractAggregatedAuditListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.scheduler.SchedulerService;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component(value="searchAuditListener")
public class SearchAuditListener
extends AbstractAggregatedAuditListener {
    private static final Duration JOB_INTERVAL = Duration.ofSeconds(5L);
    static final Duration AUDIT_SESSION_TIME = Duration.ofSeconds(2L);
    static final int MAX_AUDIT_ENTRIES_BEFORE_FLUSH = 20;
    private static final String SEARCH_PERFORMED_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("search.performed");
    private static final String JOB_RUNNER_KEY_AND_ID = "SearchAuditListener";
    public static final String AUDIT_ENTRY_18_N_KEY = "audit.logging.extra.attribute.query";

    public SearchAuditListener(AuditService auditService, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, I18nResolver i18nResolver, LocaleResolver localeResolver, @ComponentImport SchedulerService schedulerService, UserAccessor userAccessor, AuditingContext auditingContext) {
        super(schedulerService, userAccessor, auditService, eventListenerRegistrar, i18nResolver, localeResolver, AUDIT_SESSION_TIME, JOB_INTERVAL, JOB_RUNNER_KEY_AND_ID, AUDIT_ENTRY_18_N_KEY, SearchAuditListener.getAuditType(), 20, auditingContext, System::nanoTime);
    }

    @EventListener
    public void onSiteSearchAuditEvent(SiteSearchAuditEvent event) {
        if (event.getSearchPerformer() != null) {
            this.registerAudit(event.getSearchPerformer().getName(), event.getQueryString());
        }
    }

    static AuditType getAuditType() {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)CoverageLevel.FULL, (String)AuditCategories.SEARCH_CATEGORY, (String)SEARCH_PERFORMED_SUMMARY).build();
    }
}

