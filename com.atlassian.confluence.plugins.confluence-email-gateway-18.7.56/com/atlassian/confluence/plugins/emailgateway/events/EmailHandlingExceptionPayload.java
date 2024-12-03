/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.plugins.emailgateway.events;

import com.atlassian.confluence.notifications.NotificationPayload;

public interface EmailHandlingExceptionPayload
extends NotificationPayload {
    public String getEmailAddress();

    public String getEmailSubject();

    public boolean isCreatePageError();

    public boolean isAttachmentError();

    public boolean isReadOnlyModeError();
}

