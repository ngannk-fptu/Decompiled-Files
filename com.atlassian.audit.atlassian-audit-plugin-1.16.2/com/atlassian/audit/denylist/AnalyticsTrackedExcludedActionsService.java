/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.denylist;

import com.atlassian.audit.analytics.ExcludedActionsListChangedEvent;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import javax.annotation.Nonnull;

public class AnalyticsTrackedExcludedActionsService
implements ExcludedActionsService {
    private final ExcludedActionsService delegate;
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo auditPluginInfo;

    public AnalyticsTrackedExcludedActionsService(ExcludedActionsService delegate, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
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
        int previousSize = this.getExcludedActions().size();
        this.delegate.updateExcludedActions(actionToAdd, actionToDelete);
        this.publishAnalyticEvent(previousSize);
    }

    @Override
    public void replaceExcludedActions(List<String> actions) {
        int previousSize = this.getExcludedActions().size();
        this.delegate.replaceExcludedActions(actions);
        this.publishAnalyticEvent(previousSize);
    }

    private void publishAnalyticEvent(int oldCount) {
        this.eventPublisher.publish((Object)new ExcludedActionsListChangedEvent(this.getExcludedActions().size(), oldCount, this.auditPluginInfo.getPluginVersion()));
    }
}

