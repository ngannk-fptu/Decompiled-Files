/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.plugins.sharepage.notifications.payload;

import com.atlassian.confluence.notifications.NotificationPayload;
import java.util.Map;
import java.util.Set;

public interface ShareContentPayload
extends NotificationPayload {
    public Set<String> getUsers();

    public String getNote();

    public Long getEntityId();

    public Long getContextualPageId();

    public Set<String> getEmails();

    public Set<String> getOriginalRequestEmails();

    public Map<String, Set<String>> getEmailsWithGroups();

    public Set<String> getGroups();
}

