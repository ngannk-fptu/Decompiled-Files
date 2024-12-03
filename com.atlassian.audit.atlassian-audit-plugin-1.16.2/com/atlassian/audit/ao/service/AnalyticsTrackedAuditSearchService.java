/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.ao.service;

import com.atlassian.audit.analytics.SearchEvent;
import com.atlassian.audit.analytics.SearchTimeoutEvent;
import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.rest.v1.DelegatedViewTypeProvider;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnalyticsTrackedAuditSearchService
implements AuditSearchService {
    private final AuditSearchService delegate;
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo pluginInfo;
    private final HttpContext httpContext;
    private final DelegatedViewTypeProvider delegatedViewTypeProvider = new DelegatedViewTypeProvider();

    public AnalyticsTrackedAuditSearchService(AuditSearchService delegate, EventPublisher eventPublisher, AuditPluginInfo pluginInfo, HttpContext httpContext) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
        this.pluginInfo = pluginInfo;
        this.httpContext = httpContext;
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) throws TimeoutException {
        try {
            Page page = this.delegate.findBy(query, pageRequest, scanLimit);
            this.eventPublisher.publish((Object)new SearchEvent(query.getFrom().isPresent() || query.getTo().isPresent(), !query.getUserIds().isEmpty(), !query.getResources().isEmpty(), !query.getCategories().isEmpty(), !query.getActions().isEmpty(), query.getSearchText().isPresent(), scanLimit < Integer.MAX_VALUE && page.getIsLastPage(), scanLimit == Integer.MAX_VALUE, this.delegatedViewTypeProvider.getDelegatedViewType(this.httpContext), this.pluginInfo.getPluginVersion()));
            return page;
        }
        catch (TimeoutException e) {
            this.eventPublisher.publish((Object)new SearchTimeoutEvent(query.getSearchText().isPresent(), scanLimit < Integer.MAX_VALUE, this.pluginInfo.getPluginVersion()));
            throw e;
        }
    }

    public void stream(@Nonnull AuditQuery query, int offset, int limit, @Nonnull Consumer<AuditEntity> consumer) throws TimeoutException {
        this.delegate.stream(query, offset, limit, consumer);
    }

    public long count(@Nullable AuditQuery query) throws TimeoutException {
        return this.delegate.count(query);
    }
}

