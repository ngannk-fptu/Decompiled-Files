/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.license-check.failed")
public class LicenseCheckFailedEvent {
    private final String action;
    private final String reason;

    public LicenseCheckFailedEvent(String reason, String action) {
        this.reason = reason;
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public String getReason() {
        return this.reason;
    }
}

