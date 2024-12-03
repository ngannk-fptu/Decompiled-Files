/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.notifications.batch.payload;

import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;

public interface BatchingPayloadTransformer<PAYLOAD extends NotificationPayload> {
    public BatchingKey getBatchingColumnValue(PAYLOAD var1);
}

