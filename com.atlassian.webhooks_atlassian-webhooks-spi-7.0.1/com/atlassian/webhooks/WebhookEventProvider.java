/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEvent;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WebhookEventProvider {
    @Nullable
    public WebhookEvent forId(@Nonnull String var1);

    @Nonnull
    public List<WebhookEvent> getEvents();

    public int getWeight();
}

