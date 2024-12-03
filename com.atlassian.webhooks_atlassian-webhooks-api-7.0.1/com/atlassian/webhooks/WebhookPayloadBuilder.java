/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WebhookPayloadBuilder {
    @Nonnull
    public WebhookPayloadBuilder body(@Nullable byte[] var1, @Nullable String var2);

    @Nonnull
    public WebhookPayloadBuilder header(@Nonnull String var1, @Nullable String var2);
}

