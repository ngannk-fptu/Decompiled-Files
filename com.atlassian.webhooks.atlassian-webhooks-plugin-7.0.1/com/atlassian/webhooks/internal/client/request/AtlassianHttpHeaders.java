/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.Response
 *  com.atlassian.webhooks.request.WebhookResponseHeaders
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.httpclient.api.Response;
import com.atlassian.webhooks.request.WebhookResponseHeaders;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AtlassianHttpHeaders
implements WebhookResponseHeaders {
    private final Response request;

    public AtlassianHttpHeaders(@Nonnull Response request) {
        this.request = Objects.requireNonNull(request, "request");
    }

    @Nonnull
    public String getHeader(@Nonnull String name) {
        return this.request.getHeader(Objects.requireNonNull(name, "name"));
    }

    @Nonnull
    public Map<String, String> getHeaders() {
        return this.request.getHeaders();
    }
}

