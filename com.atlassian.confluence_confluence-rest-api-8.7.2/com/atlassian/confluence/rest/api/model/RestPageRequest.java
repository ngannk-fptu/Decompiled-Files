/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.rest.api.services.RestNavigation;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@ExperimentalApi
public class RestPageRequest
implements PageRequest {
    public static final String START_QPARAM = "start";
    public static final String LIMIT_QPARAM = "limit";
    public static final String CURSOR_QPARAM = "cursor";
    private final int start;
    private final Cursor cursor;
    private final int limit;
    private final UriBuilder requestUri;

    public RestPageRequest(UriInfo requestInfo, int start, int limit) {
        this(RestPageRequest.getURIBuilderFromInfo(requestInfo), start, limit);
    }

    public RestPageRequest(UriInfo requestInfo, Cursor cursor, int limit) {
        this(RestPageRequest.getURIBuilderFromInfo(requestInfo), cursor, limit);
    }

    public RestPageRequest(Navigation.Builder navBuilder, int start, int limit) {
        this(RestPageRequest.asUriBuilder(navBuilder), start, limit);
    }

    public RestPageRequest(Navigation.Builder navBuilder, Cursor cursor, int limit) {
        this(RestPageRequest.asUriBuilder(navBuilder), cursor, limit);
    }

    private static UriBuilder getURIBuilderFromInfo(UriInfo info) {
        UriBuilder builder = UriBuilder.fromUri((URI)info.getRequestUri());
        builder = builder.replaceQueryParam("os_username", (Object[])null);
        builder = builder.replaceQueryParam("os_password", (Object[])null);
        return builder;
    }

    public RestPageRequest(UriBuilder requestUri, int start, int limit) {
        this(requestUri, start, null, limit);
    }

    public RestPageRequest(UriBuilder requestUri, Cursor cursor, int limit) {
        this(requestUri, 0, cursor, limit);
    }

    protected RestPageRequest(UriBuilder requestUri, int start, Cursor cursor, int limit) {
        if (start > 0 && cursor != null) {
            throw new BadRequestException("start shouldn't be used together with cursor");
        }
        if (limit < 0 || start < 0) {
            throw new BadRequestException("limit and start must be greater than or equal to 0");
        }
        this.requestUri = requestUri;
        this.start = start;
        this.cursor = cursor;
        this.limit = limit;
    }

    public RestPageRequest(Navigation.Builder navBuilder, PageResponse<Content> response) {
        this(RestPageRequest.asUriBuilder(navBuilder), response);
    }

    private static UriBuilder asUriBuilder(Navigation.Builder navBuilder) {
        return ((RestNavigation.RestBuilder)navBuilder).toAbsoluteUriBuilder();
    }

    public RestPageRequest(UriBuilder requestUri, PageResponse response) {
        this(requestUri, response.getPageRequest().getStart(), response.getPageRequest().getCursor(), response.getPageRequest().getLimit());
    }

    public RestPageRequest copyWithLimits(PageResponse response) {
        PageRequest request = (PageRequest)Preconditions.checkNotNull((Object)response.getPageRequest(), (Object)"PageRequest should not be null");
        UriBuilder uriBuilder = UriBuilder.fromUri((URI)this.requestUri.build(new Object[0]));
        return new RestPageRequest(uriBuilder, request.getStart(), request.getCursor(), request.getLimit());
    }

    public int getStart() {
        return this.start;
    }

    public int getLimit() {
        return this.limit;
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public UriBuilder getUriBuilder() {
        return this.requestUri;
    }
}

