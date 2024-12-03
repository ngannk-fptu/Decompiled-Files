/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.publish;

import com.atlassian.webhooks.internal.publish.InternalWebhookInvocation;
import javax.annotation.Nonnull;

public interface WebhookDispatcher {
    public void dispatch(@Nonnull InternalWebhookInvocation var1);

    public int getInFlightCount();

    public long getLastRejectedTimestamp();
}

