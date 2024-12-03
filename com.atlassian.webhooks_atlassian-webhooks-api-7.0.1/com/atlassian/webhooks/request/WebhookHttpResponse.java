/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.request;

import com.atlassian.webhooks.request.WebhookResponseBody;
import com.atlassian.webhooks.request.WebhookResponseHeaders;
import java.io.Closeable;
import javax.annotation.Nonnull;

public interface WebhookHttpResponse
extends Closeable {
    @Override
    public void close();

    @Nonnull
    public WebhookResponseBody getBody();

    @Nonnull
    public WebhookResponseHeaders getHeaders();

    public int getStatusCode();
}

