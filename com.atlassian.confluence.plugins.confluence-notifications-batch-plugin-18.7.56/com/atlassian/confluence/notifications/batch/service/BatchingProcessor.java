/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.Participant
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;

@ExperimentalApi
public interface BatchingProcessor<PAYLOAD extends NotificationPayload, IMPL extends PAYLOAD, CONTEXT>
extends Participant<PAYLOAD> {
    public CONTEXT process(PAYLOAD var1, CONTEXT var2);

    public Class<IMPL> getPayloadTypeImpl();
}

