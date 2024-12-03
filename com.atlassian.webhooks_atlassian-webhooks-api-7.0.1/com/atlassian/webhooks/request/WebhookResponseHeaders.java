/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.request;

import java.util.Map;
import javax.annotation.Nonnull;

public interface WebhookResponseHeaders {
    @Nonnull
    public String getHeader(@Nonnull String var1);

    @Nonnull
    public Map<String, String> getHeaders();
}

