/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import javax.annotation.Nonnull;

public interface WebhookCallback {
    public void onError(WebhookHttpRequest var1, @Nonnull Throwable var2, @Nonnull WebhookInvocation var3);

    public void onFailure(@Nonnull WebhookHttpRequest var1, @Nonnull WebhookHttpResponse var2, @Nonnull WebhookInvocation var3);

    public void onSuccess(@Nonnull WebhookHttpRequest var1, @Nonnull WebhookHttpResponse var2, @Nonnull WebhookInvocation var3);
}

