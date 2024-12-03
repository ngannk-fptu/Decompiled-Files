/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookInvocation
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.publish;

import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.internal.client.request.RawRequest;
import java.util.List;
import javax.annotation.Nonnull;

public interface InternalWebhookInvocation
extends WebhookInvocation {
    @Nonnull
    public List<WebhookCallback> getCallbacks();

    @Nonnull
    public RawRequest.Builder getRequestBuilder();
}

