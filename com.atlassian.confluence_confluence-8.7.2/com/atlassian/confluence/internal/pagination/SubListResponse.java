/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 */
package com.atlassian.confluence.internal.pagination;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import java.util.List;

public class SubListResponse {
    public static <T> PageResponse<T> from(List<T> list, LimitedRequest request) {
        int startIndex = request.getStart();
        if (startIndex >= list.size()) {
            return PageResponseImpl.empty((boolean)false);
        }
        int limit = request.getLimit();
        int endIndex = Math.min(startIndex + limit, list.size());
        List<T> subList = list.subList(startIndex, endIndex);
        boolean hasMore = endIndex < list.size();
        return PageResponseImpl.from(subList, (boolean)hasMore).build();
    }
}

