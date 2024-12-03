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

public interface Recipient
extends Entity {
    public static final String SERVER_TYPE = "server_notification_type";

    public boolean isIndividual();

    public void setIndividual(boolean var1);

    public String getType();

    public void setType(String var1);

    public int getServerId();

    public void setServerId(int var1);

    @StringLength(value=-1)
    public String getParamValue();

    public void setParamValue(String var1);

    @StringLength(value=-1)
    public String getParamDisplay();

    public void setParamDisplay(String var1);

    public Notification getNotification();
}

