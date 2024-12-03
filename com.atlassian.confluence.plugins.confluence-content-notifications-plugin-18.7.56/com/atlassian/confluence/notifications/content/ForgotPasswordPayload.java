/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;

@ExperimentalApi
public interface ForgotPasswordPayload
extends NotificationPayload {
    public String getResetPasswordLink();

    public String getForgotPasswordLink();
}

