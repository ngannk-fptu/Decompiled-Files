/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.oauth2.client.storage.token.dao.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="CLIENT_TOKEN")
public interface AOClientToken
extends RawEntity<String> {
    public static final String ID = "ID";
    public static final String CONFIG_ID = "CONFIG_ID";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_EXPIRATION = "ACCESS_TOKEN_EXPIRATION";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String REFRESH_TOKEN_EXPIRATION = "REFRESH_TOKEN_EXPIRATION";
    public static final String STATUS = "STATUS";
    public static final String LAST_REFRESHED = "LAST_REFRESHED";
    public static final String REFRESH_COUNT = "REFRESH_COUNT";
    public static final String LAST_STATUS_UPDATED = "LAST_STATUS_UPDATED";

    @NotNull
    @PrimaryKey(value="ID")
    public String getId();

    public void setId(String var1);

    @NotNull
    public String getConfigId();

    public void setConfigId(String var1);

    @NotNull
    @StringLength(value=-1)
    public String getAccessToken();

    public void setAccessToken(String var1);

    @NotNull
    public long getAccessTokenExpiration();

    public void setAccessTokenExpiration(long var1);

    @StringLength(value=-1)
    public String getRefreshToken();

    public void setRefreshToken(String var1);

    public Long getRefreshTokenExpiration();

    public void setRefreshTokenExpiration(Long var1);

    @NotNull
    public String getStatus();

    public void setStatus(String var1);

    public Long getLastRefreshed();

    public void setLastRefreshed(Long var1);

    public int getRefreshCount();

    public void setRefreshCount(int var1);

    @NotNull
    public Long getLastStatusUpdated();

    public void setLastStatusUpdated(Long var1);
}

