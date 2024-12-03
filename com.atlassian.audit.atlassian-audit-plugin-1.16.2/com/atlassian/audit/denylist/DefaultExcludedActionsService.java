/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.denylist;

import com.atlassian.audit.denylist.ExcludedActionsProvider;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.denylist.ExcludedActionsUpdater;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DefaultExcludedActionsService
implements ExcludedActionsService {
    private final ExcludedActionsUpdater excludedActionsUpdater;
    private final ExcludedActionsProvider excludedActionsProvider;

    public DefaultExcludedActionsService(@Nonnull ExcludedActionsUpdater excludedActionsUpdater, @Nonnull ExcludedActionsProvider excludedActionsProvider) {
        this.excludedActionsUpdater = Objects.requireNonNull(excludedActionsUpdater);
        this.excludedActionsProvider = Objects.requireNonNull(excludedActionsProvider);
    }

    @Override
    public boolean shouldExclude(@Nonnull AuditEntity auditEntity) {
        Objects.requireNonNull(auditEntity);
        return this.excludedActionsProvider.getCachedExcludedActions().contains(auditEntity.getAuditType().getAction());
    }

    @Override
    @Nonnull
    public List<String> getExcludedActions() {
        return this.excludedActionsProvider.queryExcludedActions().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void updateExcludedActions(List<String> actionToAdd, List<String> actionToDelete) {
        this.excludedActionsUpdater.updateExcludedActions(actionToAdd, actionToDelete);
    }

    @Override
    public void replaceExcludedActions(List<String> actions) {
        this.excludedActionsUpdater.replaceExcludedActions(actions);
    }
}

