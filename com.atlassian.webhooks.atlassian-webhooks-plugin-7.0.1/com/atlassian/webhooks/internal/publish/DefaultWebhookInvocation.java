/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPublishRequest
 *  com.atlassian.webhooks.request.Method
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.publish;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPublishRequest;
import com.atlassian.webhooks.internal.client.request.RawRequest;
import com.atlassian.webhooks.internal.publish.InternalWebhookInvocation;
import com.atlassian.webhooks.request.Method;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class DefaultWebhookInvocation
implements InternalWebhookInvocation {
    private static final Method DEFAULT_METHOD = Method.POST;
    private final RawRequest.Builder builder;
    private final List<WebhookCallback> callbacks;
    private final WebhookEvent event;
    private final Webhook hook;
    private final Object payload;
    private final String id;

    public DefaultWebhookInvocation(@Nonnull Webhook hook, @Nonnull WebhookPublishRequest webhookRequest) {
        this(hook, UUID.randomUUID().toString(), webhookRequest);
    }

    @VisibleForTesting
    DefaultWebhookInvocation(@Nonnull Webhook hook, @Nonnull String id, @Nonnull WebhookPublishRequest webhookRequest) {
        this.hook = Objects.requireNonNull(hook, "hook");
        this.id = Objects.requireNonNull(id, "id");
        this.builder = RawRequest.builder(DEFAULT_METHOD, hook.getUrl());
        this.callbacks = new ArrayList<WebhookCallback>(Objects.requireNonNull(webhookRequest, "webhookRequest").getCallbacks());
        this.event = webhookRequest.getEvent();
        this.payload = webhookRequest.getPayload().orElse(null);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof WebhookInvocation)) {
            return false;
        }
        WebhookInvocation that = (WebhookInvocation)o;
        return com.google.common.base.Objects.equal((Object)this.id, (Object)that.getId());
    }

    @Override
    @Nonnull
    public List<WebhookCallback> getCallbacks() {
        return ImmutableList.copyOf(this.callbacks);
    }

    @Nonnull
    public WebhookEvent getEvent() {
        return this.event;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public Optional<Object> getPayload() {
        return Optional.ofNullable(this.payload);
    }

    @Override
    @Nonnull
    public RawRequest.Builder getRequestBuilder() {
        if (this.builder == null) {
            throw new IllegalStateException("Http context has not been created for this invocation");
        }
        return this.builder;
    }

    @Nonnull
    public Webhook getWebhook() {
        return this.hook;
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.id});
    }

    public void registerCallback(@Nonnull WebhookCallback callback) {
        this.callbacks.add(Objects.requireNonNull(callback, "callback"));
    }
}

