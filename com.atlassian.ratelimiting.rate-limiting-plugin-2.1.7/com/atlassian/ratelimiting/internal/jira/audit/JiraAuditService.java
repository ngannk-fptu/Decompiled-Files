/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.auditing.AuditingCategory
 *  com.atlassian.jira.auditing.AuditingManager
 *  com.atlassian.jira.auditing.ChangedValue
 *  com.atlassian.jira.auditing.RecordRequest
 */
package com.atlassian.ratelimiting.internal.jira.audit;

import com.atlassian.jira.auditing.AuditingCategory;
import com.atlassian.jira.auditing.AuditingManager;
import com.atlassian.jira.auditing.ChangedValue;
import com.atlassian.jira.auditing.RecordRequest;
import com.atlassian.ratelimiting.audit.AuditEntry;
import com.atlassian.ratelimiting.audit.AuditService;
import com.atlassian.ratelimiting.internal.jira.audit.AffectedUser;
import com.atlassian.ratelimiting.internal.jira.audit.ChangedValueAdaptor;
import java.util.Set;
import java.util.stream.Collectors;

public class JiraAuditService
implements AuditService {
    private final AuditingManager auditingManager;

    public JiraAuditService(AuditingManager auditingManager) {
        this.auditingManager = auditingManager;
    }

    @Override
    public void store(AuditEntry auditEntry) {
        RecordRequest recordRequest = new RecordRequest(AuditingCategory.GENERAL_CONFIGURATION, auditEntry.getSummary()).withChangedValues(this.toChangedValues(auditEntry));
        auditEntry.getUserProfile().map(AffectedUser::new).ifPresent(arg_0 -> ((RecordRequest)recordRequest).forObject(arg_0));
        this.auditingManager.store(recordRequest);
    }

    private Set<ChangedValue> toChangedValues(AuditEntry auditEntry) {
        return auditEntry.getChanges().stream().map(ChangedValueAdaptor::new).collect(Collectors.toSet());
    }
}

