/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.Cursor;

@ExperimentalApi
public interface PageRequest {
    public int getStart();

    public int getLimit();

    default public Cursor getCursor() {
        return null;
    }
}

