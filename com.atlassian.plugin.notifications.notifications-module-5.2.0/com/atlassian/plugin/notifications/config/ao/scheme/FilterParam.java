/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.plugin.notifications.config.ao.scheme;

import com.atlassian.plugin.notifications.config.ao.scheme.Notification;
import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

public interface FilterParam
extends Entity {
    public String getParamKey();

    @StringLength(value=-1)
    public String getParamValue();

    public Notification getNotification();
}

