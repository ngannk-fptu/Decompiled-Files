/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.service.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.google.common.base.Function;
import java.util.function.BiFunction;

@ExperimentalApi
public interface PaginationService {
    @Deprecated
    default public <H, M> PageResponse<M> doPaginationRequest(LimitedRequest pageRequest, PaginationBatch<H> fetchBatch, Function<? super H, M> modelConverter) {
        return this.performPaginationRequest(pageRequest, fetchBatch, (java.util.function.Function<? super H, M>)modelConverter);
    }

    public <H, M> PageResponse<M> performPaginationRequest(LimitedRequest var1, PaginationBatch<H> var2, java.util.function.Function<? super H, M> var3);

    @Deprecated
    default public <H, M> PageResponse<M> doPaginationListRequest(LimitedRequest pageRequest, PaginationBatch<H> fetchBatch, Function<Iterable<H>, Iterable<M>> modelConverter) {
        return this.performPaginationListRequest(pageRequest, fetchBatch, (java.util.function.Function<Iterable<H>, Iterable<M>>)modelConverter);
    }

    public <H, M> PageResponse<M> performPaginationListRequest(LimitedRequest var1, PaginationBatch<H> var2, java.util.function.Function<Iterable<H>, Iterable<M>> var3);

    default public <H, M> PageResponse<M> performPaginationListRequestWithCursor(LimitedRequest pageRequest, java.util.function.Function<LimitedRequest, PageResponse<H>> fetchBatch, java.util.function.Function<Iterable<H>, Iterable<M>> modelConverter, BiFunction<H, Boolean, Cursor> cursorCalculator) {
        throw new UnsupportedOperationException("Method PaginationService.performPaginationListRequestWithCursor is not implemented");
    }
}

