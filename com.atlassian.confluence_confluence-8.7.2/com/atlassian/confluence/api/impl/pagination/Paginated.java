/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.impl.pagination.PagingIterator;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;

@ExperimentalApi
public interface Paginated<T> {
    public PageResponse<T> page(PageRequest var1);

    public PagingIterator<T> pagingIterator();
}

