/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhooksConfiguration
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhooksConfiguration;

public interface WebhooksLifecycleAware {
    public void onStart(WebhooksConfiguration var1);

    default public void onStop() {
    }
}

