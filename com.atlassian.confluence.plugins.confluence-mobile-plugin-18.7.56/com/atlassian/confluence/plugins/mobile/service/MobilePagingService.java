/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.mobile.model.pagination.MobilePageRequest;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MobilePagingService {
    public <T, H> PageResponse<T> doPaginationListRequest(MobilePageRequest var1, Function<MobilePageRequest, List<H>> var2, Predicate<H> var3, Function<List<H>, List<T>> var4);
}

