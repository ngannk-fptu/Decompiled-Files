/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.pats.events.audit.confluence;

import com.atlassian.audit.api.AuditService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.pats.events.audit.AdvancedAuditLogHandler;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;

public class ConfluenceAdvancedAuditLogHandler
extends AdvancedAuditLogHandler {
    private final AuditService auditService;
    private final I18nResolver i18nResolver;
    private final UserAccessor userAccessor;

    public ConfluenceAdvancedAuditLogHandler(AuditService auditService, I18nResolver i18nResolver, UserAccessor userAccessor) {
        this.auditService = auditService;
        this.i18nResolver = i18nResolver;
        this.userAccessor = userAccessor;
    }

    @Override
    public void logTokenCreated(TokenEvent tokenEvent) {
        this.auditService.audit(this.auditEvent("personal.access.tokens.audit.log.summary.token.created", this.auditResource(this.userAccessor.getUserByKey(new UserKey(tokenEvent.getTokenOwnerId())).getName(), this.i18nResolver.getText("personal.access.tokens.audit.log.type"), tokenEvent.getTokenOwnerId()), tokenEvent.getTokenName()));
    }

    @Override
    public void logTokenDeleted(TokenEvent tokenEvent) {
        this.auditService.audit(this.auditEvent("personal.access.tokens.audit.log.summary.token.deleted", this.auditResource(this.userAccessor.getUserByKey(new UserKey(tokenEvent.getTokenOwnerId())).getName(), this.i18nResolver.getText("personal.access.tokens.audit.log.type"), tokenEvent.getTokenOwnerId()), tokenEvent.getTokenName()));
    }
}

