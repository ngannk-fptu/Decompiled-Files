/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.mail.Email
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEmailNotification;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.mail.Email;
import java.util.Collection;

public interface ReminderEmailNotificationBuilder {
    public ReminderEmailNotification build(Email var1, ConfluenceUser var2, Collection<ReminderEvent> var3);
}

