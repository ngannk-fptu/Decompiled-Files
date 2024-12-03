/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.exception.WebhookNotFoundException;

public interface WebhookNotificationListener {
    public void onPingSuccess(long var1) throws WebhookNotFoundException;

    public void onPingFailure(long var1) throws WebhookNotFoundException;
}

