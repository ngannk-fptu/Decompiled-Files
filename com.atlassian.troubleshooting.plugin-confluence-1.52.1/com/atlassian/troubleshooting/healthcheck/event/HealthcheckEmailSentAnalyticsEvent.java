/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.healthcheck.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="healthcheck.notification.email.sent")
public class HealthcheckEmailSentAnalyticsEvent {
    private final int recipients;

    public HealthcheckEmailSentAnalyticsEvent(int recipients) {
        this.recipients = recipients;
    }

    public int getRecipients() {
        return this.recipients;
    }
}

