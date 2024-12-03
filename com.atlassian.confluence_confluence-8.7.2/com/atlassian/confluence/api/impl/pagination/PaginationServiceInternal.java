/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.confluence.api.impl.pagination.Paginated;
import com.atlassian.confluence.api.impl.pagination.PagingIterator;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.google.common.base.Function;

public interface PaginationServiceInternal
extends PaginationService {
    @Deprecated
    default public <F, T> PagingIterator<T> createPagingIterator(PaginationBatch<F> fetchBatch, int resultsPerPage, Function<Iterable<F>, Iterable<T>> modelConverter) {
        return this.newPagingIterator(fetchBatch, resultsPerPage, (java.util.function.Function<Iterable<F>, Iterable<T>>)modelConverter);
    }

    public <F, T> PagingIterator<T> newPagingIterator(PaginationBatch<F> var1, int var2, java.util.function.Function<Iterable<F>, Iterable<T>> var3);

    @Deprecated
    default public <F, T> Paginated<T> createPaginated(PaginationBatch<F> fetchBatch, Function<Iterable<F>, Iterable<T>> modelConverter, int maxLimit) {
        return this.newPaginated(fetchBatch, (java.util.function.Function<Iterable<F>, Iterable<T>>)modelConverter, maxLimit);
    }

    public <F, T> Paginated<T> newPaginated(PaginationBatch<F> var1, java.util.function.Function<Iterable<F>, Iterable<T>> var2, int var3);
}

