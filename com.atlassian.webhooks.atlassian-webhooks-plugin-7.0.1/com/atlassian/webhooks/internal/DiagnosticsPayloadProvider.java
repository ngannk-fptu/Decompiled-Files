/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  com.atlassian.webhooks.WebhookPayloadProvider
 *  com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import com.atlassian.webhooks.WebhookPayloadProvider;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;

public class DiagnosticsPayloadProvider
implements WebhookPayloadProvider {
    public int getWeight() {
        return 100000;
    }

    public void setPayload(@Nonnull WebhookInvocation invocation, @Nonnull WebhookPayloadBuilder builder) {
        builder.body("{\"test\": true}".getBytes(StandardCharsets.UTF_8), "application/json");
    }

    public boolean supports(@Nonnull WebhookInvocation invocation) {
        return invocation.getEvent() == WebhookDiagnosticsEvent.PING;
    }
}

