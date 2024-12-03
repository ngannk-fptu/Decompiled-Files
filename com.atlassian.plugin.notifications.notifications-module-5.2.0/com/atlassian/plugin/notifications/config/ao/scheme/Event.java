/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 */
package com.atlassian.plugin.notifications.config.ao.scheme;

import com.atlassian.plugin.notifications.config.ao.scheme.Notification;
import net.java.ao.Entity;

public interface Event
extends Entity {
    public Notification getNotification();

    public String getEventKey();

    public void setEventKey(String var1);
}

