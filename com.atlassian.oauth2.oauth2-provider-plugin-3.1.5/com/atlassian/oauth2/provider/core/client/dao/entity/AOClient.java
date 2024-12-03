/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.oauth2.provider.core.client.dao.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Table(value="CLIENT")
public interface AOClient
extends RawEntity<String> {
    public static final String ID = "ID";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final String NAME = "NAME";
    public static final String USER_KEY = "USER_KEY";
    public static final String SCOPE = "SCOPE";

    @PrimaryKey(value="ID")
    @NotNull
    public String getId();

    public void setId(String var1);

    @Unique
    @NotNull
    public String getClientId();

    public void setClientId(String var1);

    @Unique
    @NotNull
    public String getClientSecret();

    public void setClientSecret(String var1);

    @Unique
    @NotNull
    public String getName();

    public void setName(String var1);

    @NotNull
    public String getUserKey();

    public void setUserKey(String var1);

    public String getScope();

    public void setScope(String var1);
}

