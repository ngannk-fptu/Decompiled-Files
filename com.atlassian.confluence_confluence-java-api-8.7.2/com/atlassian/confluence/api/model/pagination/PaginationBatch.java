/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.google.common.base.Function;

@ExperimentalApi
public interface PaginationBatch<T>
extends Function<LimitedRequest, PageResponse<T>> {
}

