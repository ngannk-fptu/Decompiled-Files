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
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.internal.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.ratelimiting.audit.AuditEntry;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Locale;
import java.util.Objects;

public class ObservabilityAuditService
implements com.atlassian.ratelimiting.audit.AuditService {
    private static final String CATEGORY_KEY = "ratelimit.audit.category.global.administration";
    private static final String USER_AFFECTED_OBJECT = "ratelimit.audit.affected.object.user";
    private final AuditService atlassianAuditService;
    private final LocaleResolver localeResolver;
    private final I18nResolver i18nResolver;

    public ObservabilityAuditService(AuditService atlassianAuditService, LocaleResolver localeResolver, I18nResolver i18nResolver) {
        this.atlassianAuditService = atlassianAuditService;
        this.localeResolver = localeResolver;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public void store(AuditEntry auditEntry) {
        AuditType auditType = new AuditType(CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, this.translate(CATEGORY_KEY), this.translate(auditEntry.getSummary()), CoverageLevel.ADVANCED);
        AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)auditType);
        auditEntry.getChanges().stream().map(change -> new ChangedValue(change.getName(), (String)change.getFrom().orElse(null), (String)change.getTo().orElse(null))).forEach(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).changedValue(arg_0));
        auditEntry.getUserProfile().ifPresent(userProfile -> auditEventBuilder.affectedObject(AuditResource.builder((String)userProfile.getFullName(), (String)this.translate(Locale.ENGLISH, USER_AFFECTED_OBJECT)).id(userProfile.getUserKey().getStringValue()).build()));
        this.atlassianAuditService.audit(auditEventBuilder.build());
    }

    private String translate(String key) {
        return this.translate(this.localeResolver.getLocale((UserKey)null), key);
    }

    private String translate(Locale locale, String key) {
        return this.i18nResolver.getText(locale, Objects.requireNonNull(key));
    }
}

