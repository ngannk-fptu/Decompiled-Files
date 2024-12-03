/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 */
package com.atlassian.confluence.plugins.mobile.activeobject.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

public interface PushNotificationAO
extends RawEntity<String> {
    public static final String ID = "ID";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String APP_NAME = "APP_NAME";
    public static final String TOKEN = "TOKEN";
    public static final String ENDPOINT = "ENDPOINT";
    public static final String GROUP_SETTING = "GROUP_SETTING";
    public static final String CUSTOM_SETTING = "CUSTOM_SETTING";
    public static final String ACTIVE = "ACTIVE";
    public static final String STATUS_UPDATED_TIME = "STATUS_UPDATED_TIME";

    @NotNull
    @PrimaryKey(value="ID")
    public String getId();

    @NotNull
    @Indexed
    public String getUserName();

    @NotNull
    public String getAppName();

    public String getToken();

    public String getDeviceId();

    public String getEndpoint();

    public String getGroupSetting();

    public String getCustomSetting();

    public boolean isActive();

    @Indexed
    public Long getStatusUpdatedTime();

    public void setId(String var1);

    public void setUserName(String var1);

    public void setAppName(String var1);

    public void setBuild(String var1);

    public void setToken(String var1);

    public void setDeviceId(String var1);

    public void setEndpoint(String var1);

    public void setGroupSetting(String var1);

    public void setCustomSetting(String var1);

    public void setActive(boolean var1);

    public void setStatusUpdatedTime(Long var1);
}

