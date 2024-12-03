/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.finder;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;

public interface ManyFetcher<T> {
    public PageResponse<T> fetchMany(PageRequest var1);
}

