/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookCreateRequest
 *  com.atlassian.webhooks.WebhookSearchRequest
 *  com.atlassian.webhooks.WebhookUpdateRequest
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.webhooks.WebhookCreateRequest;
import com.atlassian.webhooks.WebhookSearchRequest;
import com.atlassian.webhooks.WebhookUpdateRequest;
import com.atlassian.webhooks.internal.dao.ao.AoWebhook;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WebhookDao {
    @Nonnull
    public AoWebhook create(@Nonnull WebhookCreateRequest var1);

    public boolean delete(int var1);

    public void delete(@Nonnull AoWebhook[] var1);

    @Nullable
    public AoWebhook getById(int var1);

    @Nonnull
    public AoWebhook[] search(@Nonnull WebhookSearchRequest var1);

    @Nullable
    public AoWebhook update(int var1, @Nonnull WebhookUpdateRequest var2);
}

