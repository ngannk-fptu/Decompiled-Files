/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.webhook.Webhook
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.model.webhook.Webhook;

public interface WebhookHealthStrategy {
    public Webhook registerSuccess(Webhook var1);

    public Webhook registerFailure(Webhook var1);

    public boolean isInGoodStanding(Webhook var1);
}

