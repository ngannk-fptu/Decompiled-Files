/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ContentEntityExpander
extends AbstractRecursiveEntityExpander<ContentEntity> {
    private RestContentManager restContentManager;
    private TransactionTemplate transactionTemplate;

    public ContentEntityExpander(TransactionTemplate transactionTemplate, RestContentManager restContentManager) {
        this.transactionTemplate = transactionTemplate;
        this.restContentManager = restContentManager;
    }

    protected ContentEntity expandInternal(ContentEntity entity) {
        return (ContentEntity)this.transactionTemplate.execute(() -> this.restContentManager.expand(entity));
    }
}

