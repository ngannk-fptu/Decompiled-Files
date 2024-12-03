/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.plugin.notifications.config.ao;

import com.atlassian.plugin.notifications.config.ao.ServerConfig;
import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

public interface ServerParam
extends Entity {
    public String getParamKey();

    @StringLength(value=-1)
    public String getParamValue();

    public void setParamValue(String var1);

    public ServerConfig getServerConfig();
}

