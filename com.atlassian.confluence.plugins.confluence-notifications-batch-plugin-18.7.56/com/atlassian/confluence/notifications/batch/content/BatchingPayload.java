/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.LinkedHashMap;
import java.util.Set;

@ExperimentalApi
public interface BatchingPayload
extends NotificationPayload {
    public Set<UserKey> getOriginators();

    public String getBatchingId();

    public String getContentType();

    public LinkedHashMap<ModuleCompleteKey, Object> getPayloads();
}

