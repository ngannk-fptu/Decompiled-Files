/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.auditing.AssociatedItem
 *  com.atlassian.jira.auditing.AuditingCategory
 *  com.atlassian.jira.auditing.AuditingManager
 *  com.atlassian.jira.auditing.ChangedValue
 *  com.atlassian.jira.auditing.RecordRequest
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.pats.events.audit.jira;

import com.atlassian.jira.auditing.AssociatedItem;
import com.atlassian.jira.auditing.AuditingCategory;
import com.atlassian.jira.auditing.AuditingManager;
import com.atlassian.jira.auditing.ChangedValue;
import com.atlassian.jira.auditing.RecordRequest;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.events.audit.jira.TokenAssociatedItem;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.sal.api.message.I18nResolver;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JiraLegacyAuditLogHandler
implements AuditLogHandler {
    private final AuditingManager auditingManager;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;

    public JiraLegacyAuditLogHandler(AuditingManager auditingManager, I18nResolver i18nResolver, UserManager userManager) {
        this.auditingManager = auditingManager;
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
    }

    @Override
    public void logTokenCreated(TokenEvent tokenEvent) {
        this.auditingManager.store(this.recordRequest(this.i18nResolver.getText("personal.access.tokens.audit.log.summary.token.created"), tokenEvent.getTokenOwnerId(), tokenEvent.getTokenName()));
    }

    @Override
    public void logTokenDeleted(TokenEvent tokenEvent) {
        this.auditingManager.store(this.recordRequest(this.i18nResolver.getText("personal.access.tokens.audit.log.summary.token.deleted"), tokenEvent.getTokenOwnerId(), tokenEvent.getTokenName()));
    }

    private RecordRequest recordRequest(String summary, String tokenOwnerId, String tokenName) {
        return new RecordRequest(AuditingCategory.USER_MANAGEMENT, summary).forObject((AssociatedItem)new TokenAssociatedItem(tokenOwnerId, this.userManager.getUserByKey(tokenOwnerId).getUsername())).withChangedValues(new ChangedValue[]{new TokenNameChangedValue(tokenName)});
    }

    public class TokenNameChangedValue
    implements ChangedValue {
        private final String tokenName;

        public TokenNameChangedValue(String tokenName) {
            this.tokenName = tokenName;
        }

        @Nonnull
        public String getName() {
            return JiraLegacyAuditLogHandler.this.i18nResolver.getText("personal.access.tokens.audit.log.extra.attribute.name");
        }

        @Nullable
        public String getFrom() {
            return null;
        }

        @Nullable
        public String getTo() {
            return this.tokenName;
        }
    }
}

