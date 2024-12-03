/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import java.util.List;

@ExperimentalApi
public interface PageResponse<T>
extends Iterable<T> {
    public List<T> getResults();

    public int size();

    public boolean hasMore();

    public PageRequest getPageRequest();

    default public Cursor getNextCursor() {
        return null;
    }

    default public Cursor getPrevCursor() {
        return null;
    }
}

