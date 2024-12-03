/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface Webhook {
    @Nonnull
    public Map<String, String> getConfiguration();

    @Nonnull
    public Date getCreatedDate();

    @Nonnull
    public Set<WebhookEvent> getEvents();

    public int getId();

    @Nonnull
    public String getName();

    @Nonnull
    public WebhookScope getScope();

    @Nonnull
    public Date getUpdatedDate();

    @Nonnull
    public String getUrl();

    public boolean isActive();
}

