/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.plugin.ModuleCompleteKey;

@ExperimentalApi
public interface PayloadProcessor {
    public <SOURCE, PAYLOAD extends NotificationPayload> boolean process(PAYLOAD var1, PayloadTransformer<SOURCE, PAYLOAD> var2, ModuleCompleteKey var3);
}

