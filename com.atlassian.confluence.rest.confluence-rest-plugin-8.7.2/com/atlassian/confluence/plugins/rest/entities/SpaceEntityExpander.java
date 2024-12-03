/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.manager.RestSpaceManager;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class SpaceEntityExpander
extends AbstractRecursiveEntityExpander<SpaceEntity> {
    private final TransactionTemplate transactionTemplate;
    private final RestSpaceManager restSpaceManager;

    public SpaceEntityExpander(TransactionTemplate transactionTemplate, RestSpaceManager restSpaceManager) {
        this.transactionTemplate = transactionTemplate;
        this.restSpaceManager = restSpaceManager;
    }

    protected SpaceEntity expandInternal(SpaceEntity entity) {
        return (SpaceEntity)this.transactionTemplate.execute(() -> this.restSpaceManager.expand(entity));
    }
}

