/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;

@ExperimentalApi
public interface Participant<PAYLOAD extends NotificationPayload> {
    public Class<PAYLOAD> getPayloadType();
}

