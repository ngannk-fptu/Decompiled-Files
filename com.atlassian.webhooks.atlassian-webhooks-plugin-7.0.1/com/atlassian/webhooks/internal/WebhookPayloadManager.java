/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import javax.annotation.Nonnull;

public interface WebhookPayloadManager {
    public void setPayload(@Nonnull WebhookInvocation var1, @Nonnull WebhookPayloadBuilder var2);
}

