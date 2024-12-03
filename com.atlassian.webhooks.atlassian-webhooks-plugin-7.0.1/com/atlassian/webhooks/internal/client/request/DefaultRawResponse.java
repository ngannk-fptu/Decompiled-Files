/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.Response
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.atlassian.webhooks.request.WebhookResponseBody
 *  com.atlassian.webhooks.request.WebhookResponseHeaders
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.httpclient.api.Response;
import com.atlassian.webhooks.internal.client.request.AtlassianHttpHeaders;
import com.atlassian.webhooks.internal.client.request.AtlassianHttpResponseBody;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.atlassian.webhooks.request.WebhookResponseBody;
import com.atlassian.webhooks.request.WebhookResponseHeaders;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DefaultRawResponse
implements WebhookHttpResponse {
    private final Response result;
    private final int statusCode;
    private final long maxBytes;

    public DefaultRawResponse(@Nonnull Response result, long maxBytes) {
        this.result = Objects.requireNonNull(result, "result");
        this.statusCode = result.getStatusCode();
        this.maxBytes = maxBytes;
    }

    public void close() {
    }

    @Nonnull
    public WebhookResponseBody getBody() {
        return new AtlassianHttpResponseBody(this.result, this.maxBytes);
    }

    @Nonnull
    public WebhookResponseHeaders getHeaders() {
        return new AtlassianHttpHeaders(this.result);
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

