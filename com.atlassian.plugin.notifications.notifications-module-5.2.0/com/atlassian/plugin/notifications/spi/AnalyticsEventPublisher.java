/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;

@ExperimentalApi
public interface AnalyticsEventPublisher {
    public void publishEvent(Object var1, Option<UserKey> var2);
}

