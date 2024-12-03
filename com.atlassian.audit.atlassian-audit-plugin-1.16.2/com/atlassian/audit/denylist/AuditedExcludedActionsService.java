/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.denylist;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.coverage.AuditedCoverageConfigService;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class AuditedExcludedActionsService
implements ExcludedActionsService {
    private final ExcludedActionsService delegate;
    private final AuditService auditService;

    public AuditedExcludedActionsService(ExcludedActionsService delegate, AuditService auditService) {
        this.delegate = delegate;
        this.auditService = auditService;
    }

    @Override
    public boolean shouldExclude(@Nonnull AuditEntity auditEntity) {
        return this.delegate.shouldExclude(auditEntity);
    }

    @Override
    @Nonnull
    public List<String> getExcludedActions() {
        return this.delegate.getExcludedActions();
    }

    @Override
    public void updateExcludedActions(List<String> actionToAdd, List<String> actionToDelete) {
        List<String> oldActions = this.delegate.getExcludedActions();
        ArrayList<String> updatedActions = new ArrayList<String>(oldActions);
        updatedActions.addAll(actionToAdd);
        updatedActions.removeAll(actionToDelete);
        this.auditService.audit(this.createDenyListUpdatedAuditEvent(oldActions.toString(), ((Object)updatedActions).toString()));
        this.delegate.updateExcludedActions(actionToAdd, actionToDelete);
    }

    @Override
    public void replaceExcludedActions(List<String> actions) {
        List<String> oldActions = this.delegate.getExcludedActions();
        this.auditService.audit(this.createDenyListUpdatedAuditEvent(oldActions.toString(), actions.toString()));
        this.delegate.replaceExcludedActions(actions);
    }

    private AuditEvent createDenyListUpdatedAuditEvent(String oldActions, String updatedActions) {
        return AuditEvent.builder((AuditType)AuditedCoverageConfigService.AUDIT_CONFIG_UPDATED).changedValue(ChangedValue.fromI18nKeys((String)"atlassian.audit.event.change.deny.list").from(oldActions).to(updatedActions).build()).build();
    }
}

