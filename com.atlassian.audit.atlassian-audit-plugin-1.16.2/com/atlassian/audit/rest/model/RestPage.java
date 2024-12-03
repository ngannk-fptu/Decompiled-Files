/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.UriInfo
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonUnwrapped
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.rest.model.RestPageCursor;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class RestPage<T, C> {
    @Nonnull
    private final List<T> values;
    @Nullable
    private final PagingInfo<C> pagingInfo;

    protected RestPage(@Nonnull List<T> entities) {
        this.values = entities;
        this.pagingInfo = null;
    }

    protected <E> RestPage(Page<? extends E, C> page, Function<E, ? extends T> restTransform, String baseUrl, UriInfo uriInfo) {
        this.values = (List)page.getValues().stream().map(restTransform).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        this.pagingInfo = new PagingInfo<Object>(page, baseUrl, uriInfo, this::serializeCursor);
    }

    @Nonnull
    @JsonIgnore
    protected List<T> getValues() {
        return this.values;
    }

    @Nonnull
    @JsonIgnore
    protected abstract RestPageCursor serializeCursor(@Nonnull C var1);

    @Nullable
    @JsonProperty(value="pagingInfo")
    public PagingInfo<C> getPagingInfo() {
        return this.pagingInfo;
    }

    private static class PagingInfo<C> {
        private final Page<?, C> page;
        private final UriInfo uriInfo;
        private final String prevalentScheme;
        private final Function<C, RestPageCursor> cursorSerializator;

        public PagingInfo(Page<?, C> page, String baseUrl, UriInfo uriInfo, Function<C, RestPageCursor> cursorSerializator) {
            this.page = page;
            this.uriInfo = uriInfo;
            this.prevalentScheme = "https".equalsIgnoreCase(UriBuilder.fromPath((String)baseUrl).build(new Object[0]).getScheme()) ? "https" : uriInfo.getBaseUri().getScheme();
            this.cursorSerializator = cursorSerializator;
        }

        @JsonProperty(value="lastPage")
        public boolean isLastPage() {
            return this.page.getIsLastPage();
        }

        @JsonProperty(value="size")
        public int getSize() {
            return this.page.getSize();
        }

        @JsonProperty(value="nextPageOffset")
        @Nullable
        public Integer getNextPageOffset() {
            return this.page.getNextPageRequest().map(PageRequest::getOffset).orElse(null);
        }

        @JsonProperty(value="nextPageCursor")
        @JsonUnwrapped
        @Nullable
        public RestPageCursor getNextPageCursor() {
            return this.page.getNextPageRequest().flatMap(PageRequest::getCursor).map(this.cursorSerializator).orElse(null);
        }

        @JsonProperty(value="nextPageLink")
        @Nullable
        public String getNextPageLink() {
            return this.page.getNextPageRequest().map(nextPageRequest -> {
                UriBuilder uriBuilder = this.uriInfo.getRequestUriBuilder();
                nextPageRequest.getCursor().ifPresent(cursor -> uriBuilder.replaceQueryParam("pageCursor", new Object[]{this.cursorSerializator.apply(cursor).getCursor()}));
                uriBuilder.replaceQueryParam("offset", new Object[]{nextPageRequest.getOffset()});
                uriBuilder.scheme(this.prevalentScheme);
                return uriBuilder.build(new Object[0]).toASCIIString();
            }).orElse(null);
        }
    }
}

