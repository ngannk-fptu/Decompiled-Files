/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Page
 *  com.atlassian.diagnostics.PageRequest
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Page;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.internal.rest.RestLink;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;

public class RestLinkUtils {
    private RestLinkUtils() {
        throw new IllegalStateException("RestLinkUtils should not be instantiated");
    }

    @Nonnull
    public static List<RestLink> linksFor(@Nonnull Supplier<UriBuilder> uriBuilderSupplier, @Nonnull Page<?> page) {
        return RestLinkUtils.linksFor(uriBuilderSupplier, page.getPrevRequest().orElse(null), page.getNextRequest().orElse(null));
    }

    @Nonnull
    public static List<RestLink> linksFor(@Nonnull Supplier<UriBuilder> uriBuilderSupplier, PageRequest prevRequest, PageRequest nextRequest) {
        ImmutableList.Builder builder = ImmutableList.builder();
        if (prevRequest != null) {
            builder.add((Object)RestLink.previous(RestLinkUtils.linkFor(uriBuilderSupplier, prevRequest)));
        }
        if (nextRequest != null) {
            builder.add((Object)RestLink.next(RestLinkUtils.linkFor(uriBuilderSupplier, nextRequest)));
        }
        return builder.build();
    }

    @Nonnull
    public static URI linkFor(@Nonnull Supplier<UriBuilder> uriBuilderSupplier, @Nonnull PageRequest pageRequest) {
        UriBuilder builder = Objects.requireNonNull(uriBuilderSupplier, "uriBuilderSupplier").get().replaceQueryParam("start", new Object[0]).replaceQueryParam("end", new Object[0]);
        int start = pageRequest.getStart();
        if (start > 0) {
            builder.replaceQueryParam("start", new Object[]{start});
        }
        builder.replaceQueryParam("limit", new Object[]{pageRequest.getLimit()});
        return builder.build(new Object[0]);
    }
}

