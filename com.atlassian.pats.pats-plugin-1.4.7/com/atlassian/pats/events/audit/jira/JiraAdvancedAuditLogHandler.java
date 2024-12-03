/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.pats.events.audit.jira;

import com.atlassian.audit.api.AuditService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.pats.events.audit.AdvancedAuditLogHandler;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.sal.api.message.I18nResolver;

public class JiraAdvancedAuditLogHandler
extends AdvancedAuditLogHandler {
    private final AuditService auditService;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;

    public JiraAdvancedAuditLogHandler(AuditService auditService, I18nResolver i18nResolver, UserManager userManager) {
        this.auditService = auditService;
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
    }

    @Override
    public void logTokenCreated(TokenEvent tokenEvent) {
        this.auditService.audit(this.auditEvent("personal.access.tokens.audit.log.summary.token.created", this.auditResource(this.userManager.getUserByKey(tokenEvent.getTokenOwnerId()).getUsername(), this.i18nResolver.getText("personal.access.tokens.audit.log.type"), tokenEvent.getTokenOwnerId()), tokenEvent.getTokenName()));
    }

    @Override
    public void logTokenDeleted(TokenEvent tokenEvent) {
        this.auditService.audit(this.auditEvent("personal.access.tokens.audit.log.summary.token.deleted", this.auditResource(this.userManager.getUserByKey(tokenEvent.getTokenOwnerId()).getUsername(), this.i18nResolver.getText("personal.access.tokens.audit.log.type"), tokenEvent.getTokenOwnerId()), tokenEvent.getTokenName()));
    }
}

