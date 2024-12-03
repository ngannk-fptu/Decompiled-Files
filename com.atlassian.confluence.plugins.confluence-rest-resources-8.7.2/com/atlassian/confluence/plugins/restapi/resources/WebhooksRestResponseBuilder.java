/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.webhooks.internal.rest.RestResponseBuilder
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.webhooks.internal.rest.RestResponseBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriInfo;

class WebhooksRestResponseBuilder
implements RestResponseBuilder {
    WebhooksRestResponseBuilder() {
    }

    public Object error(String fieldName, @Nonnull String errorMessage) {
        return ImmutableMap.of((Object)"message", (Object)errorMessage);
    }

    public Object page(@Nonnull UriInfo uriInfo, int start, int limit, @Nonnull List<?> values, boolean hasMore) {
        return RestList.newRestList().pageRequest((PageRequest)new RestPageRequest(uriInfo, start, limit)).results(values, hasMore).build();
    }
}

