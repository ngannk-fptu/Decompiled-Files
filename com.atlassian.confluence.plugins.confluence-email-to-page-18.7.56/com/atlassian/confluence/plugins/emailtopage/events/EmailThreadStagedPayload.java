/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.plugins.emailtopage.events;

import com.atlassian.confluence.notifications.NotificationPayload;

public interface EmailThreadStagedPayload
extends NotificationPayload {
    public String getPageTitle();

    public String getHash();

    public boolean isError();
}

