/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditEntity;
import javax.annotation.Nonnull;

public class ExcludedActionsAwareAuditBroker
implements InternalAuditBroker {
    private final InternalAuditBroker delegatedBroker;
    private final ExcludedActionsService excludedActionsService;

    public ExcludedActionsAwareAuditBroker(InternalAuditBroker delegatedBroker, ExcludedActionsService excludedActionsService) {
        this.delegatedBroker = delegatedBroker;
        this.excludedActionsService = excludedActionsService;
    }

    @Override
    public void audit(@Nonnull AuditEntity entity) {
        if (!this.isExcluded(entity)) {
            this.delegatedBroker.audit(entity);
        }
    }

    private boolean isExcluded(AuditEntity entity) {
        return this.excludedActionsService.shouldExclude(entity);
    }
}

