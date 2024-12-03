/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEventId;
import javax.annotation.Nonnull;

public interface WebhookEvent {
    @Nonnull
    @WebhookEventId
    public String getId();

    @Nonnull
    public String getI18nKey();
}

