/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.event.notification;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface NotificationEnabledEvent {
    public boolean isSuppressNotifications();
}

