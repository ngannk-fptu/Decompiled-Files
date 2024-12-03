/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.request;

import com.atlassian.webhooks.request.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WebhookHttpRequest {
    @Nullable
    public byte[] getContent();

    @Nonnull
    public Optional<String> getContentType();

    @Nonnull
    public Map<String, String> getHeaders();

    @Nonnull
    public Method getMethod();

    @Nonnull
    public Map<String, List<String>> getQueryParameters();

    @Nonnull
    public String getUrl();

    public static interface Builder {
        @Nonnull
        public WebhookHttpRequest build();

        @Nullable
        public byte[] getBody();

        @Nonnull
        public Map<String, String> getHeaders();

        @Nonnull
        public Method getMethod();

        @Nonnull
        public Map<String, List<String>> getParameters();

        @Nonnull
        public String getUrl();

        @Nonnull
        public Builder header(@Nonnull String var1, @Nonnull String var2);

        @Nonnull
        public Builder method(@Nonnull Method var1);

        @Nonnull
        public Builder parameter(@Nonnull String var1, String ... var2);

        @Nonnull
        public Builder removeHeader(@Nonnull String var1);

        @Nonnull
        public Builder url(@Nonnull String var1);

        @Nonnull
        public Builder url(@Nonnull URI var1);
    }
}

