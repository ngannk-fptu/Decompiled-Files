/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.OneToMany
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.plugin.notifications.config.ao.scheme;

import com.atlassian.plugin.notifications.config.ao.scheme.Notification;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.schema.StringLength;

public interface NotificationScheme
extends Entity {
    @Accessor(value="SCHEME_NAME")
    public String getName();

    @StringLength(value=-1)
    public String getDescription();

    @Mutator(value="SCHEME_NAME")
    public void setName(String var1);

    public void setDescription(String var1);

    @OneToMany(reverse="getNotificationScheme")
    public Notification[] getNotifications();
}

