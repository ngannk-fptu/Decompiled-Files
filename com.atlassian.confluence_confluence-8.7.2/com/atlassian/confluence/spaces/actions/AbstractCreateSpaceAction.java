/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;

public abstract class AbstractCreateSpaceAction
extends AbstractSpaceAction {
    protected static final String PRIVATE = "private";
    protected IndexManager indexManager;

    public IndexManager getIndexManager() {
        return this.indexManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}

