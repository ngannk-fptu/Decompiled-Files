/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.oauth2.client.storage.config.dao.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Table(value="CLIENT_CONFIG")
public interface AOClientConfig
extends RawEntity<String> {
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String TYPE = "TYPE";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final String AUTHORIZATION_ENDPOINT = "AUTHORIZATION_ENDPOINT";
    public static final String TOKEN_ENDPOINT = "TOKEN_ENDPOINT";
    public static final String SCOPES = "SCOPES";

    @NotNull
    @PrimaryKey(value="ID")
    public String getId();

    public void setId(String var1);

    @Unique
    @NotNull
    public String getName();

    public void setName(String var1);

    public String getDescription();

    public void setDescription(String var1);

    @NotNull
    public String getType();

    public void setType(String var1);

    @NotNull
    public String getClientId();

    public void setClientId(String var1);

    @NotNull
    public String getClientSecret();

    public void setClientSecret(String var1);

    @NotNull
    public String getAuthorizationEndpoint();

    public void setAuthorizationEndpoint(String var1);

    @NotNull
    public String getTokenEndpoint();

    public void setTokenEndpoint(String var1);

    @NotNull
    @StringLength(value=-1)
    public String getScopes();

    public void setScopes(String var1);
}

