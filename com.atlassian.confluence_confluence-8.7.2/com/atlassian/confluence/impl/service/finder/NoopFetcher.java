/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.finder.ManyFetcher
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 */
package com.atlassian.confluence.impl.service.finder;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import java.util.Optional;

public class NoopFetcher<T>
implements SingleFetcher<T>,
ManyFetcher<T> {
    public PageResponse<T> fetchMany(PageRequest request) {
        return PageResponseImpl.empty((boolean)false, (PageRequest)request);
    }

    public Optional<T> fetch() {
        return Optional.empty();
    }

    public T fetchOneOrNull() {
        return null;
    }
}

