/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.mobile.model.pagination.MobilePageRequest;
import com.atlassian.confluence.plugins.mobile.service.MobilePagingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class MobilePagingServiceImpl
implements MobilePagingService {
    private static final int MAXIMUM_REQUEST_VALUE = 3;

    @Override
    public <T, H> PageResponse<T> doPaginationListRequest(@Nonnull MobilePageRequest initialPageRequest, @Nonnull Function<MobilePageRequest, List<H>> executeService, @Nonnull Predicate<H> filter, @Nonnull Function<List<H>, List<T>> converter) {
        boolean hasMore;
        if (initialPageRequest.getLimit() < initialPageRequest.getAccept()) {
            throw new BadRequestException("limit value must to greater or equal accept value.");
        }
        ArrayList result = new ArrayList();
        int numOfRequest = 0;
        MobilePageRequest pageRequest = new MobilePageRequest(initialPageRequest);
        do {
            hasMore = false;
            List<H> executeResult = executeService.apply(pageRequest);
            if (!Objects.isNull(executeResult)) {
                hasMore = executeResult.size() == pageRequest.getLimit();
                List filterList = executeResult.stream().filter(filter).collect(Collectors.toList());
                result.addAll(converter.apply(filterList));
            }
            pageRequest.setStart(pageRequest.getStart() + pageRequest.getLimit());
        } while (hasMore && ++numOfRequest < 3 && result.size() < pageRequest.getAccept());
        pageRequest.setNext(new MobilePageRequest(pageRequest.getStart() + pageRequest.getLimit(), pageRequest.getLimit(), pageRequest.getAccept()));
        return PageResponseImpl.from(result, (boolean)hasMore).pageRequest((PageRequest)pageRequest).build();
    }
}

