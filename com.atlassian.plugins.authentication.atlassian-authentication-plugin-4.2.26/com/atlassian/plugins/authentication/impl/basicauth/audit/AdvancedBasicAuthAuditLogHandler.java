/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.ChangedValue$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.basicauth.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.basicauth.audit.BasicAuthAuditLogHandler;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class AdvancedBasicAuthAuditLogHandler
implements BasicAuthAuditLogHandler {
    public static final String BASIC_AUTH_ENABLED_TITLE_I18N_KEY = "authentication.basic.auth.audit.log.change.basic.auth.enabled.title";
    private final AuditService auditService;

    @Inject
    public AdvancedBasicAuthAuditLogHandler(@ComponentImport AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void logDoNotBlockBasicAuthRequests() {
        this.auditService.audit(this.auditEvent("authentication.basic.auth.audit.log.summary.basic.auth.enabled", this.getChangedValue(true, false, BASIC_AUTH_ENABLED_TITLE_I18N_KEY)));
    }

    @Override
    public void logBlockingBasicAuthRequests() {
        this.auditService.audit(this.auditEvent("authentication.basic.auth.audit.log.summary.basic.auth.disabled", this.getChangedValue(false, true, BASIC_AUTH_ENABLED_TITLE_I18N_KEY)));
    }

    @Override
    public void logAllowedPathsChange(@Nonnull Set<String> currentAllowlist, @Nonnull Set<String> updatedAllowlist) {
        this.auditService.audit(this.auditEvent("authentication.basic.auth.audit.log.change.allowlist.paths.update", this.getChangedValue(currentAllowlist, updatedAllowlist, "authentication.basic.auth.audit.log.change.allowlist.paths.title")));
    }

    @Override
    public void logAllowedUsersChange(@Nonnull Set<String> currentAllowlist, @Nonnull Set<String> updatedAllowlist) {
        this.auditService.audit(this.auditEvent("authentication.basic.auth.audit.log.change.allowlist.users.update", this.getChangedValue(currentAllowlist, updatedAllowlist, "authentication.basic.auth.audit.log.change.allowlist.users.title")));
    }

    public <T> ChangedValue getChangedValue(T oldValue, T newValue, String i18nKey) {
        return new ChangedValue.Builder(i18nKey).from(oldValue.toString()).to(newValue.toString()).build();
    }

    private AuditEvent auditEvent(String actionI18nKey, ChangedValue changedValue) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.BASE, (String)"authentication.basic.auth.audit.log.category", (String)actionI18nKey).build()).changedValue(changedValue).build();
    }
}

