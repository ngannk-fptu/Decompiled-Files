/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl$Builder
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerException
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import com.google.common.base.Function;

public class PagerToPageResponseHelper {
    @Deprecated
    public static <H, M> PageResponse<M> fromPager(Pager<H> pager, LimitedRequest limitedRequest, Function<H, M> transfromer) throws PagerException {
        if (limitedRequest.getStart() > 0) {
            pager.skipTo(limitedRequest.getStart());
        }
        PageResponseImpl.Builder response = PageResponseImpl.builder().pageRequest(limitedRequest);
        int count = 0;
        for (Object hibernateObject : pager) {
            if (count >= limitedRequest.getLimit()) {
                response.hasMore(true);
                break;
            }
            response.add(transfromer.apply(hibernateObject));
            ++count;
        }
        return response.build();
    }

    public static <H, M> PageResponse<M> createFromPager(Pager<H> pager, LimitedRequest limitedRequest, java.util.function.Function<H, M> transformer) throws PagerException {
        return PagerToPageResponseHelper.fromPager(pager, limitedRequest, transformer::apply);
    }
}

