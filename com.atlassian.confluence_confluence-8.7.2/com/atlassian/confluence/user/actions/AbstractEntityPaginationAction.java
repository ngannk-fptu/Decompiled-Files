/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.user.Entity
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.user.Entity;

public abstract class AbstractEntityPaginationAction<T extends Entity>
extends ConfluenceActionSupport {
    protected PaginationSupport<T> paginationSupport;

    public AbstractEntityPaginationAction() {
        this.paginationSupport = new PaginationSupport();
    }

    public AbstractEntityPaginationAction(int itemsPerPage) {
        this.paginationSupport = new PaginationSupport(itemsPerPage);
    }

    public int getStartIndex() {
        return this.paginationSupport.getStartIndex();
    }

    public void setStartIndex(int startIndex) {
        this.paginationSupport.setStartIndex(startIndex);
    }

    public PaginationSupport<T> getPaginationSupport() {
        return this.paginationSupport;
    }
}

