/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditQuery$Builder
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.service.audit.AuditService$AuditRecordFinder
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 */
package com.atlassian.confluence.api.impl.service.audit.adapter;

import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.internal.audit.AuditFormatConverter;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Deprecated
class AuditRecordFinderAdapter
implements AuditService.AuditRecordFinder {
    private final AuditSearchService searchService;
    private final AuditFormatConverter formatConverter;
    private final PaginationService paginationService;
    private final Instant from;
    private final Instant to;
    private final String searchString;

    AuditRecordFinderAdapter(AuditSearchService searchService, AuditFormatConverter formatConverter, PaginationService paginationService, Instant from, Instant to, String searchString) {
        this.searchService = searchService;
        this.formatConverter = formatConverter;
        this.paginationService = paginationService;
        this.from = from;
        this.to = to;
        this.searchString = searchString;
    }

    public AuditService.AuditRecordFinder withSearchString(String searchString) {
        return new AuditRecordFinderAdapter(this.searchService, this.formatConverter, this.paginationService, this.from, this.to, searchString);
    }

    public PageResponse<AuditRecord> fetchMany(PageRequest request) {
        PaginationBatch fetchPage = nextRequest -> {
            try {
                AuditQuery.Builder queryBuilder = AuditQuery.builder().from(this.from).to(this.to);
                if (this.searchString != null) {
                    queryBuilder = queryBuilder.searchText(this.searchString);
                }
                return PageResponseImpl.filteredResponse((LimitedRequest)Objects.requireNonNull(nextRequest), (List)this.searchService.findBy(queryBuilder.build(), new PageRequest.Builder().offset(request.getStart()).limit(request.getLimit()).build()).getValues(), x -> true);
            }
            catch (TimeoutException e) {
                throw new RuntimeException("Timed out serving the search request", e);
            }
        };
        return this.paginationService.performPaginationRequest(LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.auditRecords()), fetchPage, this.formatConverter::toAuditRecord);
    }
}

