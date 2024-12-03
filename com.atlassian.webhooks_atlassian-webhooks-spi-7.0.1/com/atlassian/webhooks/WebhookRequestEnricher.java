/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookInvocation;
import javax.annotation.Nonnull;

public interface WebhookRequestEnricher {
    public void enrich(@Nonnull WebhookInvocation var1);

    public int getWeight();
}

