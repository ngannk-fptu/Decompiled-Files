/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import javax.annotation.Nonnull;

public interface WebhookPayloadProvider {
    public int getWeight();

    public void setPayload(@Nonnull WebhookInvocation var1, @Nonnull WebhookPayloadBuilder var2);

    public boolean supports(@Nonnull WebhookInvocation var1);
}

