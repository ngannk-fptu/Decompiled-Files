/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.DispatchFailedException
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookStatistics
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.DispatchFailedException;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookStatistics;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class SimpleWebhooksStatistics
implements WebhookStatistics {
    private final WebhookCallback callback = new StatisticsCallback();
    private final AtomicLong errorCount = new AtomicLong();
    private final AtomicLong failureCount = new AtomicLong();
    private final AtomicLong publishCount = new AtomicLong();
    private final AtomicLong rejectedCount = new AtomicLong();
    private final AtomicLong successCount = new AtomicLong();

    SimpleWebhooksStatistics() {
    }

    public long getDispatchErrorCount() {
        return this.errorCount.get();
    }

    public long getDispatchFailureCount() {
        return this.failureCount.get();
    }

    public long getDispatchRejectedCount() {
        return this.rejectedCount.get();
    }

    public long getDispatchSuccessCount() {
        return this.successCount.get();
    }

    public long getDispatchedCount() {
        return this.successCount.get() + this.errorCount.get() + this.failureCount.get();
    }

    public long getPublishedCount() {
        return this.publishCount.get();
    }

    WebhookCallback asCallback() {
        return this.callback;
    }

    void onPublish() {
        this.publishCount.incrementAndGet();
    }

    class StatisticsCallback
    implements WebhookCallback {
        StatisticsCallback() {
        }

        public void onError(WebhookHttpRequest request, @Nonnull Throwable error, @Nonnull WebhookInvocation invocation) {
            if (error instanceof DispatchFailedException) {
                SimpleWebhooksStatistics.this.rejectedCount.incrementAndGet();
            } else {
                SimpleWebhooksStatistics.this.errorCount.incrementAndGet();
            }
        }

        public void onFailure(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation invocation) {
            SimpleWebhooksStatistics.this.failureCount.incrementAndGet();
        }

        public void onSuccess(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation invocation) {
            SimpleWebhooksStatistics.this.successCount.incrementAndGet();
        }
    }
}

