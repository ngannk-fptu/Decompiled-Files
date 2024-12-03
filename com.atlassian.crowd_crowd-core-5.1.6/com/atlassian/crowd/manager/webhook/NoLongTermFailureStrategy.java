/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.atlassian.crowd.model.webhook.WebhookTemplate
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.manager.webhook.WebhookHealthStrategy;
import com.atlassian.crowd.model.webhook.Webhook;
import com.atlassian.crowd.model.webhook.WebhookTemplate;
import com.google.common.annotations.VisibleForTesting;
import java.time.Clock;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NoLongTermFailureStrategy
implements WebhookHealthStrategy {
    public static final long DEFAULT_MIN_FAILURES = 50L;
    public static final long DEFAULT_MIN_INTERVAL_MILLIS = TimeUnit.HOURS.toMillis(6L);
    private final long minFailures;
    private final long minIntervalMillis;
    private final Clock clock;

    public NoLongTermFailureStrategy() {
        this(50L, DEFAULT_MIN_INTERVAL_MILLIS);
    }

    public NoLongTermFailureStrategy(long minFailures, long minIntervalMillis) {
        this(minFailures, minIntervalMillis, Clock.systemUTC());
    }

    @VisibleForTesting
    NoLongTermFailureStrategy(long minFailures, long minIntervalMillis, Clock clock) {
        this.minFailures = minFailures;
        this.minIntervalMillis = minIntervalMillis;
        this.clock = clock;
    }

    @Override
    public Webhook registerSuccess(Webhook webhook) {
        WebhookTemplate webhookTemplate = new WebhookTemplate(webhook);
        webhookTemplate.resetFailuresSinceLastSuccess();
        webhookTemplate.resetOldestFailureDate();
        return webhookTemplate;
    }

    @Override
    public Webhook registerFailure(Webhook webhook) {
        WebhookTemplate webhookTemplate = new WebhookTemplate(webhook);
        webhookTemplate.setFailuresSinceLastSuccess(webhook.getFailuresSinceLastSuccess() + 1L);
        if (webhook.getOldestFailureDate() == null) {
            webhookTemplate.setOldestFailureDate(new Date(this.clock.millis()));
        }
        return webhookTemplate;
    }

    @Override
    public boolean isInGoodStanding(Webhook webhook) {
        return webhook.getOldestFailureDate() == null || webhook.getFailuresSinceLastSuccess() < this.getMinFailures() || this.clock.millis() - webhook.getOldestFailureDate().getTime() < this.getMinIntervalMillis();
    }

    public long getMinFailures() {
        return this.minFailures;
    }

    public long getMinIntervalMillis() {
        return this.minIntervalMillis;
    }
}

