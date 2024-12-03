/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.OneToMany
 */
package com.atlassian.plugin.notifications.config.ao.scheme;

import com.atlassian.plugin.notifications.config.ao.scheme.Event;
import com.atlassian.plugin.notifications.config.ao.scheme.FilterParam;
import com.atlassian.plugin.notifications.config.ao.scheme.NotificationScheme;
import com.atlassian.plugin.notifications.config.ao.scheme.Recipient;
import net.java.ao.Entity;
import net.java.ao.OneToMany;

public interface Notification
extends Entity {
    @OneToMany
    public FilterParam[] getFilterParams();

    public NotificationScheme getNotificationScheme();

    @OneToMany
    public Recipient[] getRecipients();

    @OneToMany
    public Event[] getEvents();
}

